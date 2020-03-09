package com.roberttisma.tools.intermediate_song_importer.service;

import static com.google.common.collect.Lists.partition;
import static com.roberttisma.tools.intermediate_song_importer.Factory.createDBUpdater;
import static com.roberttisma.tools.intermediate_song_importer.Factory.createMigrationService;
import static com.roberttisma.tools.intermediate_song_importer.exceptions.ImporterException.checkImporter;
import static com.roberttisma.tools.intermediate_song_importer.model.report.FinalReport.createFinalReport;
import static com.roberttisma.tools.intermediate_song_importer.util.FileIO.listFilesInDir;
import static com.roberttisma.tools.intermediate_song_importer.util.FileIO.writeStringToFile;
import static com.roberttisma.tools.intermediate_song_importer.util.JsonUtils.toPrettyJson;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.stream.Collectors.toUnmodifiableList;

import com.roberttisma.tools.intermediate_song_importer.model.ProfileConfig;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.roberttisma.tools.intermediate_song_importer.model.report.FinalReport;
import com.roberttisma.tools.intermediate_song_importer.model.report.Report;
import com.roberttisma.tools.intermediate_song_importer.model.report.SuccessReport;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

@Builder
@RequiredArgsConstructor
public class ProcessService implements Runnable {

  @NonNull private final ProfileConfig profileConfig;
  @NonNull private final Path inputDir;
  @NonNull private final Path outputReportFile;
  private final int numThreads;

  @Override
  @SneakyThrows
  public void run() {
    val executorService = newFixedThreadPool(numThreads);
    val files = listFilesInDir(inputDir, true);
    try (val dbUpdater = createDBUpdater(profileConfig.getTargetSong().getDb())) {
      val service =
          createMigrationService(
              profileConfig.getSourceSong(), profileConfig.getTargetSong(), dbUpdater);

      // Initialize all the studyIds on the target song
      service.initTargetStudyIds(files);

      // Concurrently run migrations
      val futures = partition(files, numThreads)
          .stream()
          .map(p -> executorService.submit(createMigrationJob(service, p)))
          .collect(toUnmodifiableList());
      executorService.shutdown();
      executorService.awaitTermination(1L, TimeUnit.DAYS);
      finalizeReport(futures);

    }
  }

  @SneakyThrows
  private void finalizeReport(Collection<Future<List<Report>>> futures){
    val isNotAllDone = futures.stream().anyMatch(f -> !f.isDone());
    checkImporter(!isNotAllDone, "Not all of the threads completed processing before being terminated");
    val reports = futures.stream()
        .map(ProcessService::extractReportData)
        .flatMap(Collection::stream)
        .collect(toUnmodifiableList());
    val finalReport = createFinalReport(reports);
    val content = toPrettyJson(finalReport);
    writeStringToFile(content, outputReportFile);
  }

  @SneakyThrows
  private static List<Report> extractReportData(Future<List<Report>> future){
    return future.get();
  }

  private static Callable<List<Report>> createMigrationJob(MigrationService service, List<Path> jsonFiles) {
    return () -> jsonFiles.stream()
        .map(service::migrate)
        .collect(toUnmodifiableList());
  }

}
