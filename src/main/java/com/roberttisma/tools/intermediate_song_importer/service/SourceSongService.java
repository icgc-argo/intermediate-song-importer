package com.roberttisma.tools.intermediate_song_importer.service;

import bio.overture.song.core.model.FileDTO;
import bio.overture.song.sdk.SongApi;
import com.fasterxml.jackson.databind.JsonNode;
import com.roberttisma.tools.intermediate_song_importer.model.SongConfig;
import com.roberttisma.tools.intermediate_song_importer.model.SourceData;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.val;

import java.nio.file.Path;
import java.util.List;

import static com.roberttisma.tools.intermediate_song_importer.exceptions.ImporterException.checkImporter;
import static com.roberttisma.tools.intermediate_song_importer.util.JsonUtils.mapper;
import static com.roberttisma.tools.intermediate_song_importer.util.RestClient.get;
import static java.lang.String.format;

@Value
@Builder
public class SourceSongService {

  @NonNull private SongConfig config;
  @NonNull private SongApi api;

  public List<FileDTO> getSourceAnalysisFiles(@NonNull Path payloadFile){
    val sourceData = processSourceData(payloadFile);
    return getSourceAnalysisFiles(sourceData);
  }

  private List<FileDTO> getSourceAnalysisFiles(SourceData d) {
    return api.getAnalysisFiles(d.getStudyId(), d.getAnalysisId());
  }

  private SourceData processSourceData(Path file) {
    val analysisId = parseAnalysisId(file);
    val studyId = getStudyForAnalysisId(analysisId);
    return SourceData.builder().analysisId(analysisId).studyId(studyId).build();
  }

  @SneakyThrows
  private String getStudyForAnalysisId(@NonNull String analysisId) {
    val response = get(getLegacyEntityUrl(analysisId));
    val j = mapper().readTree(response.getBody());
    val contentNode = parseContent(j);
    checkImporter(!contentNode.isEmpty(),
        "The analysisId '%s' does not exist at '%s'",
        analysisId, getConfig().getServerUrl());
    return contentNode.path(0).path("projectCode").asText();
  }

  private String getLegacyEntityUrl(String analysisId) {
    return format("%s/entities?gnosId=%s", config.getServerUrl(), analysisId);
  }

  private static String parseAnalysisId(Path file) {
    checkFileNameFormat(file);
    return file.getFileName().toString().replaceAll("\\.json$", "");
  }

  private static void checkFileNameFormat(Path targetPayloadFile) {
    checkImporter(
        targetPayloadFile.toString().endsWith(".json"),
        "The file '%s' is not a json file",
        targetPayloadFile.toString());
  }

  private static JsonNode parseContent(JsonNode root){
    checkRequiredField(root, "content");
    return root.path("content");
  }

  private static void checkRequiredField(JsonNode j, String field){
    checkImporter(j.has(field), "Could not find field '%s' in %", field, j.toString());
  }

}
