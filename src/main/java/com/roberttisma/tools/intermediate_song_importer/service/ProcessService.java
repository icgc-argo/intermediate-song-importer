package com.roberttisma.tools.intermediate_song_importer.service;

import static com.google.common.collect.Lists.partition;
import static com.roberttisma.tools.intermediate_song_importer.Factory.createMigrationService;
import static com.roberttisma.tools.intermediate_song_importer.Factory.createRepository;
import static com.roberttisma.tools.intermediate_song_importer.exceptions.ImporterException.checkImporter;
import static com.roberttisma.tools.intermediate_song_importer.model.report.FinalReport.createFinalReport;
import static com.roberttisma.tools.intermediate_song_importer.util.FileIO.listFilesInDir;
import static com.roberttisma.tools.intermediate_song_importer.util.FileIO.writeStringToFile;
import static com.roberttisma.tools.intermediate_song_importer.util.JsonUtils.toPrettyJson;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.stream.Collectors.toUnmodifiableList;

import com.roberttisma.tools.intermediate_song_importer.model.ProfileConfig;
import com.roberttisma.tools.intermediate_song_importer.model.id.GenomicEntity;
import com.roberttisma.tools.intermediate_song_importer.model.report.IdDNEErrorReport;
import com.roberttisma.tools.intermediate_song_importer.model.report.Report;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Builder
@Slf4j
@RequiredArgsConstructor
public class ProcessService implements Runnable {

  @NonNull private final AnalysisTypeValidationService analysisTypeValidationService;
  @NonNull private final IdValidationService idValidationService;
  @NonNull private final ProfileConfig profileConfig;
  @NonNull private final Path inputDir;
  @NonNull private final Path outputReportFile;

  private final int numThreads;

  @Override
  @SneakyThrows
  public void run() {
    val files = listFilesInDir(inputDir, true);
    try (val dbUpdater = createRepository(profileConfig.getTargetSong().getDb())) {
      val service =
          createMigrationService(
              profileConfig.getSourceSong(), profileConfig.getTargetSong(), dbUpdater);

      // Initialize all the studyIds on the target song
      log.info("Initializing target studyIds");
      service.initTargetStudyIds(files);

      // Check if all the submitterIds from the batch exist on the external id service that
      // targetSong communicates with
      log.info(
          "Validating that ALL submitterIds exist on external id service before beginning the migration");
      val idFailedReports = extractReportData(idValidationService.validate(files));
      if (!idFailedReports.isEmpty()) {
        log.error("[ID_VALIDATION_ERROR]: Several ids do not exist on the external id service");
        writeFinalReport(idFailedReports);
        return;
      }
      log.info(
          "[ID_VALIDATION_SUCCESS]: All the submitterIds from '{}' exist on the external id service connected to targetSong",
          inputDir.toString());

      // Check if all analysisTypes already exist and are the latest
      log.info("Validating that all analysisTypes in all payloads are the latest");
      val analysisTypeErrorReports =
          analysisTypeValidationService.validateLatestAnalysisType(files);
      if (!analysisTypeErrorReports.isEmpty()) {
        writeFinalReport(analysisTypeErrorReports);
        return;
      }

      // Concurrently run migrations
      log.info("Starting migrations");
      val executorService = newFixedThreadPool(numThreads);
      val futures =
          partition(files, numThreads).stream()
              .map(p -> executorService.submit(createMigrationJob(service, p)))
              .collect(toUnmodifiableList());
      executorService.shutdown();
      executorService.awaitTermination(1L, TimeUnit.DAYS);
      log.info("Finished migrations");
      finalizeMigrationReport(futures);
    }
  }

  private void finalizeMigrationReport(Collection<Future<List<Report>>> futures) {
    val isNotAllDone = futures.stream().anyMatch(f -> !f.isDone());
    checkImporter(
        !isNotAllDone, "Not all of the threads completed processing before being terminated");
    val reports =
        futures.stream()
            .map(ProcessService::extractReportData)
            .flatMap(Collection::stream)
            .collect(toUnmodifiableList());
    writeFinalReport(reports);
  }

  @SneakyThrows
  private void writeFinalReport(List<Report> reports) {
    val finalReport = createFinalReport(reports);
    val content = toPrettyJson(finalReport);
    writeStringToFile(content, outputReportFile);
  }

  @SneakyThrows
  private static List<Report> extractReportData(Future<List<Report>> future) {
    return future.get();
  }

  @SneakyThrows
  private static List<Report> extractReportData(Stream<GenomicEntity> failedGenomicEntityStream) {
    return failedGenomicEntityStream
        .map(
            x ->
                IdDNEErrorReport.builder()
                    .errorType("id.does.not.exist")
                    .entityType(x.getGenomicType().name())
                    .studyId(x.getStudyId())
                    .submitterId(x.getSubmitterId())
                    .build())
        .collect(toUnmodifiableList());
  }

  private static Callable<List<Report>> createMigrationJob(
      MigrationService service, List<Path> jsonFiles) {
    return () -> jsonFiles.stream().map(service::migrate).collect(toUnmodifiableList());
  }
}
