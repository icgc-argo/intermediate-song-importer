package com.roberttisma.tools.intermediate_song_importer.service;

import static com.roberttisma.tools.intermediate_song_importer.exceptions.ImporterException.checkImporter;
import static com.roberttisma.tools.intermediate_song_importer.util.CollectionUtils.mapToSet;
import static com.roberttisma.tools.intermediate_song_importer.util.CollectionUtils.mapToStream;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Collectors.toUnmodifiableSet;

import bio.overture.song.core.model.Analysis;
import bio.overture.song.core.model.FileDTO;
import com.roberttisma.tools.intermediate_song_importer.Repository;
import com.roberttisma.tools.intermediate_song_importer.model.report.ErrorReport;
import com.roberttisma.tools.intermediate_song_importer.model.report.Report;
import com.roberttisma.tools.intermediate_song_importer.model.report.SuccessReport;
import com.roberttisma.tools.intermediate_song_importer.util.Payloads;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
@Builder
@RequiredArgsConstructor
public class MigrationService {

  @NonNull private final SourceSongService sourceSongService;
  @NonNull private final TargetSongService targetSongService;
  @NonNull private final Repository repository;

  public void initTargetStudyIds(@NonNull Collection<Path> jsonFiles) {
    jsonFiles.stream()
        .map(Payloads::parseStudyId)
        .collect(toSet())
        .forEach(targetSongService::saveStudy);
  }

  @SneakyThrows
  public Report migrate(@NonNull Path jsonFile) {
    try {
      // Get source files
      val sourceAnalysisFiles = sourceSongService.getSourceAnalysisFiles(jsonFile);

      // Get target analysis
      val targetAnalysis = targetSongService.submitTargetPayload(jsonFile);

      // Update object ids via backdoor db
      updateAnalysisFiles(sourceAnalysisFiles, targetAnalysis.getFiles());

      // Publish the target analysis
      targetSongService.publishTargetAnalysis(targetAnalysis);

      return buildSuccessReport(jsonFile, sourceAnalysisFiles, targetAnalysis);
    } catch (Throwable t) {
      log.error(
          "[PROCESSING_ERROR] filename='{}' errorType='{}':  '{}",
          jsonFile.toString(),
          t.getClass().getSimpleName(),
          t.getMessage());
      return ErrorReport.builder()
          .payloadFilename(jsonFile.toString())
          .errorType(t.getClass().getName())
          .message(t.getMessage())
          .build();
    }
  }

  private SuccessReport buildSuccessReport(
      Path jsonFile, List<FileDTO> sourceAnalysisFiles, Analysis targetAnalysis) {
    val sourceAnalysisIds =
        mapToStream(sourceAnalysisFiles, FileDTO::getAnalysisId).collect(toUnmodifiableSet());
    val sourceStudyId = mapToStream(sourceAnalysisFiles, FileDTO::getStudyId).findFirst().get();
    val sourceObjectIds = mapToSet(sourceAnalysisFiles, FileDTO::getObjectId);
    val targetObjectIds =
        mapToSet(
            targetSongService.getTargetAnalysisFiles(
                targetAnalysis.getStudyId(), targetAnalysis.getAnalysisId()),
            FileDTO::getObjectId);
    val targetAnalysisId = targetAnalysis.getAnalysisId();
    val targetAnalysisState =
        targetSongService.getTargetAnalysisState(targetAnalysis.getStudyId(), targetAnalysisId);
    val allObjectsMigrated = sourceObjectIds.containsAll(targetObjectIds);
    val reportData =
        SuccessReport.builder()
            .payloadFilename(jsonFile.toString())
            .legacyStudyId(sourceStudyId)
            .legacyAnalysisIds(sourceAnalysisIds)
            .targetAnalysisId(targetAnalysisId)
            .targetAnalysisState(targetAnalysisState)
            .targetStudyId(targetAnalysis.getStudyId())
            .isAllObjectIdsMigrated(allObjectsMigrated)
            .objectIdsMigrated(targetObjectIds)
            .build();
    log.info("[PROCESSING_SUCCESS]  {}", reportData);
    return reportData;
  }

  private void updateAnalysisFiles(List<FileDTO> sourceFiles, List<FileDTO> targetFiles) {
    val sourceMap = sourceFiles.stream().collect(toMap(FileDTO::getFileName, FileDTO::getObjectId));
    val numLinesChanges =
        targetFiles.stream()
            .mapToInt(
                t -> {
                  checkImporter(
                      sourceMap.containsKey(t.getFileName()),
                      "No matching filename '%s' for target analysisId '%s'",
                      t.getFileName(),
                      t.getAnalysisId());
                  val sourceObjectId = sourceMap.get(t.getFileName());
                  repository.updateFileInfoId(sourceObjectId, t.getObjectId());
                  return repository.updateFileId(sourceObjectId, t.getObjectId());
                })
            .sum();

    // Assert all files were updated
    checkImporter(
        numLinesChanges == targetFiles.size(),
        "There are %s files, however only %s were updated",
        targetFiles.size(),
        numLinesChanges);
  }
}
