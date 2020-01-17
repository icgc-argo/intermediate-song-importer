package com.roberttisma.tools.intermediate_song_importer.service;

import bio.overture.song.core.model.Analysis;
import bio.overture.song.core.model.FileDTO;
import bio.overture.song.sdk.SongApi;
import com.roberttisma.tools.intermediate_song_importer.DBUpdater;
import com.roberttisma.tools.intermediate_song_importer.model.SourceData;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.Closeable;
import java.nio.file.Path;
import java.util.List;

import static com.roberttisma.tools.intermediate_song_importer.exceptions.ImporterException.checkImporter;
import static com.roberttisma.tools.intermediate_song_importer.util.FileIO.readFileContent;
import static com.roberttisma.tools.intermediate_song_importer.util.JsonUtils.mapper;
import static java.util.stream.Collectors.toMap;

@Value
@Slf4j
@Builder
public class MigrationService implements Closeable {

  @NonNull private final StudyService sourceStudyService;
  @NonNull private final StudyService targetStudyService;
  @NonNull private final SongApi sourceApi;
  @NonNull private final SongApi targetApi;
  @NonNull private final DBUpdater dbUpdater;

  @SneakyThrows
  public void migrate(@NonNull Path jsonFile) {
    try {
      // get source data
      val sourceData = processSourceData(jsonFile);

      // Get source files
      val sourceAnalysisFiles = getSourceAnalysisFiles(sourceData);

      // Get target analysis
      val targetAnalysis = submitTargetPayload(jsonFile);

      // Update object ids via backdoor db
      updateAnalysisFiles(sourceAnalysisFiles, targetAnalysis.getFiles());

      // Publish the target analysis
      publishTargetAnalysis(targetAnalysis);

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

  private void publishTargetAnalysis(Analysis target) {
    targetApi.publish(target.getStudyId(), target.getAnalysisId(), false);
  }

  @SneakyThrows
  private Analysis submitTargetPayload(Path jsonFile) {
    val targetPayload = readFileContent(jsonFile);
    val targetStudyId = extractStudyId(targetPayload);

    // Create the study if it does not exist
    if (!targetStudyService.isStudyExist(targetStudyId)) {
      targetStudyService.createStudy(targetStudyId);
    }

    val targetAnalysisId = targetApi.submit(targetStudyId, targetPayload).getAnalysisId();
    return targetApi.getAnalysis(targetStudyId, targetAnalysisId);
  }

  private List<FileDTO> getSourceAnalysisFiles(SourceData d) {
    return sourceApi.getAnalysisFiles(d.getStudyId(), d.getAnalysisId());
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

  private SourceData processSourceData(Path file) {
    val analysisId = parseAnalysisId(file);
    val studyId = sourceStudyService.getStudyForAnalysisId(analysisId);
    return SourceData.builder().analysisId(analysisId).studyId(studyId).build();
  }

  private static String parseAnalysisId(Path file) {
    checkFileNameFormat(file);
    return file.getFileName().toString().replaceAll("\\.json$", "");
  }

  @SneakyThrows
  private static String extractStudyId(String payload) {
    val j= mapper().readTree(payload);
    checkImporter(j.has("studyId"), "json missing the studyId field");
    return j.path("studyId").asText();
  }

  private static void checkFileNameFormat(Path targetPayloadFile) {
    checkImporter(
        targetPayloadFile.toString().endsWith(".json"),
        "The file '%s' is not a json file",
        targetPayloadFile.toString());
  }
}
