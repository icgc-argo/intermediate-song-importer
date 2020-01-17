package com.roberttisma.tools.intermediate_song_importer.service;

import bio.overture.song.core.model.FileDTO;
import com.roberttisma.tools.intermediate_song_importer.DBUpdater;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.Closeable;
import java.nio.file.Path;
import java.util.List;

import static com.roberttisma.tools.intermediate_song_importer.exceptions.ImporterException.checkImporter;
import static java.util.stream.Collectors.toMap;

@Slf4j
@Builder
@RequiredArgsConstructor
public class MigrationService implements Closeable {

  @NonNull private final SourceSongService sourceSongService;
  @NonNull private final TargetSongService targetSongService;
  @NonNull private final DBUpdater dbUpdater;

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

      log.info("[PROCESSING_SUCCESS] filename='{}'", jsonFile.toString());
    } catch (Throwable t) {
      log.error(
          "[PROCESSING_ERROR] filename='{}' errorType='{}':  '{}",
          jsonFile.toString(),
          t.getClass().getSimpleName(),
          t.getMessage());
    }
  }

  @Override
  public void close() {
    dbUpdater.close();
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
