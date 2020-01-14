package com.roberttisma.tools.intermediate_song_importer.service;

import static com.roberttisma.tools.intermediate_song_importer.exceptions.ImporterException.checkImporter;
import static com.roberttisma.tools.intermediate_song_importer.util.FileIO.readFileContent;
import static com.roberttisma.tools.intermediate_song_importer.util.JsonUtils.mapper;
import static java.lang.String.format;
import static java.util.stream.Collectors.toMap;

import bio.overture.song.core.model.Analysis;
import bio.overture.song.core.model.FileDTO;
import bio.overture.song.sdk.SongApi;
import com.fasterxml.jackson.databind.JsonNode;
import com.roberttisma.tools.intermediate_song_importer.DBUpdater;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
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
      val sourceAnalysis = sourceApi.getAnalysis(studyId, sourceAnalysisId);

      // Submit target payload to target song
      val targetAnalysisId = targetApi.submit(studyId, payloadAsString).getAnalysisId();

      // Update object ids via backdoor db
      val targetAnalysis = targetApi.getAnalysis(studyId, targetAnalysisId);
      updateAnalysisFiles(sourceAnalysis, targetAnalysis);

      // Publish
      targetApi.publish(studyId, targetAnalysisId, false);
    } catch (Throwable t) {
      val message =
          format(
              "[PROCESSING_ERROR] filename='%s' errorType='%s':  '%s",
              jsonFile.toString(), t.getClass().getSimpleName(), t.getMessage());
      log.error(message);
    }
  }

  @Override
  public void close() throws IOException {
    dbUpdater.close();
  }

  private void updateAnalysisFiles(Analysis source, Analysis target) {
    val targetMap =
        target.getFiles().stream().collect(toMap(FileDTO::getFileName, FileDTO::getObjectId));
    source.getFiles().stream()
        .map(
            s -> {
              checkImporter(
                  targetMap.containsKey(s.getFileName()),
                  "No matching filename '%s' for source analysisId '%s' and target analysisId '%s'",
                  s.getFileName(),
                  s.getAnalysisId(),
                  target.getAnalysisId());
              val targetObjectId = targetMap.get(s.getFileName());
              return dbUpdater.update(s.getObjectId(), targetObjectId);
            });
  }

  private static String extractAnalysisId(Path file) {
    return file.getFileName().toString().replaceAll("\\.json$", "");
  }

  private static String extractStudyId(JsonNode j) {
    checkImporter(j.has("studyId"), "json missing the studyId field");
    return j.path("studyId").asText();
  }
}
