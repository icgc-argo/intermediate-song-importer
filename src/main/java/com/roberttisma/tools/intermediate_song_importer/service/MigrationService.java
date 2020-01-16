package com.roberttisma.tools.intermediate_song_importer.service;

import static com.roberttisma.tools.intermediate_song_importer.exceptions.ImporterException.checkImporter;
import static com.roberttisma.tools.intermediate_song_importer.util.FileIO.readFileContent;
import static com.roberttisma.tools.intermediate_song_importer.util.JsonUtils.mapper;
import static java.util.stream.Collectors.toMap;

import bio.overture.song.core.model.FileDTO;
import bio.overture.song.sdk.SongApi;
import com.fasterxml.jackson.databind.JsonNode;
import com.roberttisma.tools.intermediate_song_importer.DBUpdater;
import java.io.Closeable;
import java.nio.file.Path;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Value
@Slf4j
@Builder
public class MigrationService implements Closeable {

  @NonNull private final SongApi sourceApi;
  @NonNull private final SongApi targetApi;
  @NonNull private final DBUpdater dbUpdater;

  @SneakyThrows
  public void migrate(@NonNull Path jsonFile) {
    try {
      checkImporter(
          jsonFile.toString().endsWith(".json"),
          "The file '%s' is not a json file",
          jsonFile.toString());
      // Read source file and get study
      val payloadAsString = readFileContent(jsonFile);
      val payloadAsJson = mapper().readTree(payloadAsString);
      val studyId = extractStudyId(payloadAsJson);

      // Read source analysis
      val sourceAnalysisId = extractAnalysisId(jsonFile);
      val sourceAnalysisFiles = sourceApi.getAnalysisFiles(studyId, sourceAnalysisId);

      // Submit target payload to target song
      val targetAnalysisId = targetApi.submit(studyId, payloadAsString).getAnalysisId();
      val targetAnalysisFiles = targetApi.getAnalysisFiles(studyId, targetAnalysisId);

      // Update object ids via backdoor db
      val numLinesChanges = updateAnalysisFiles(sourceAnalysisFiles, targetAnalysisFiles);

      // Assert all files were updated
      checkImporter(
          numLinesChanges == targetAnalysisFiles.size(),
          "There are %s files, however only %s were updated",
          targetAnalysisFiles.size(),
          numLinesChanges);

      // Publish the target analysis
      targetApi.publish(studyId, targetAnalysisId, false);

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

  private int updateAnalysisFiles(List<FileDTO> sourceFiles, List<FileDTO> targetFiles) {
    val targetMap = targetFiles.stream().collect(toMap(FileDTO::getFileName, FileDTO::getObjectId));
    return sourceFiles.stream()
        .mapToInt(
            s -> {
              checkImporter(
                  targetMap.containsKey(s.getFileName()),
                  "No matching filename '%s' for source analysisId '%s' and target analysisId '%s'",
                  s.getFileName(),
                  s.getAnalysisId(),
                  s.getAnalysisId());
              val targetObjectId = targetMap.get(s.getFileName());
              return dbUpdater.update(s.getObjectId(), targetObjectId);
            })
        .sum();
  }

  private static String extractAnalysisId(Path file) {
    return file.getFileName().toString().replaceAll("\\.json$", "");
  }

  private static String extractStudyId(JsonNode j) {
    checkImporter(j.has("studyId"), "json missing the studyId field");
    return j.path("studyId").asText();
  }
}
