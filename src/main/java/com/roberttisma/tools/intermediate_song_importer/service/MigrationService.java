package com.roberttisma.tools.intermediate_song_importer.service;

import static bio.overture.song.core.utils.Separators.COMMA;
import static com.roberttisma.tools.intermediate_song_importer.exceptions.ImporterException.checkImporter;
import static com.roberttisma.tools.intermediate_song_importer.util.CollectionUtils.mapToSet;
import static com.roberttisma.tools.intermediate_song_importer.util.CollectionUtils.mapToStream;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import bio.overture.song.core.model.Analysis;
import bio.overture.song.core.model.FileDTO;
import com.roberttisma.tools.intermediate_song_importer.DBUpdater;
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
  @NonNull private final DBUpdater dbUpdater;

  public void initTargetStudyIds(@NonNull Collection<Path> jsonFiles) {
    jsonFiles.stream()
        .map(Payloads::parseStudyId)
        .collect(toSet())
        .forEach(targetSongService::saveStudy);
  }

  @SneakyThrows
  public void migrate(@NonNull Path jsonFile) {
    try {
      // Get source files
      val sourceAnalysisFiles = sourceSongService.getSourceAnalysisFiles(jsonFile);

      // Get target analysis
      val targetAnalysis = targetSongService.submitTargetPayload(jsonFile);

      // Update object ids via backdoor db
      updateAnalysisFiles(sourceAnalysisFiles, targetAnalysis.getFiles());

      // Publish the target analysis
      targetSongService.publishTargetAnalysis(targetAnalysis);

      report(jsonFile, sourceAnalysisFiles, targetAnalysis);
    } catch (Throwable t) {
      log.error(
          "[PROCESSING_ERROR] filename='{}' errorType='{}':  '{}",
          jsonFile.toString(),
          t.getClass().getSimpleName(),
          t.getMessage());
    }
  }

  private void report(Path jsonFile, List<FileDTO> sourceAnalysisFiles, Analysis targetAnalysis) {
    val sourceAnalysisId =
        mapToStream(sourceAnalysisFiles, FileDTO::getAnalysisId).findFirst().get();
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
    log.info(
        "[PROCESSING_SUCCESS] fn={}\tsourceAnId={}\tsourceStudyId={}\ttargetAnId={}\ttargetStudyId={}\ttargetAnState={}\tallObjectIdsMigrated={}\tobjectIdsMigrated=[{}]",
        jsonFile.toString(),
        sourceAnalysisId,
        sourceStudyId,
        targetAnalysisId,
        targetAnalysis.getStudyId(),
        targetAnalysisState,
        allObjectsMigrated,
        COMMA.join(targetObjectIds));
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
                  return dbUpdater.update(sourceObjectId, t.getObjectId());
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
