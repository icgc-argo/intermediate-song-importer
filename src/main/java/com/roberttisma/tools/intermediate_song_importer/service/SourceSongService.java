package com.roberttisma.tools.intermediate_song_importer.service;

import static com.roberttisma.tools.intermediate_song_importer.exceptions.ImporterException.checkImporter;
import static com.roberttisma.tools.intermediate_song_importer.util.JsonUtils.checkRequiredField;
import static com.roberttisma.tools.intermediate_song_importer.util.JsonUtils.mapper;
import static com.roberttisma.tools.intermediate_song_importer.util.RestClient.get;
import static java.lang.String.format;
import static java.util.stream.Collectors.toUnmodifiableList;

import bio.overture.song.core.model.FileDTO;
import bio.overture.song.sdk.SongApi;
import com.fasterxml.jackson.databind.JsonNode;
import com.roberttisma.tools.intermediate_song_importer.model.ImporterSpec;
import com.roberttisma.tools.intermediate_song_importer.model.SongConfig;
import com.roberttisma.tools.intermediate_song_importer.model.SourceData;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

@Builder
@RequiredArgsConstructor
public class SourceSongService {

  @NonNull private SongApi api;
  @NonNull private SongConfig config;

  public List<FileDTO> getSourceAnalysisFiles(@NonNull ImporterSpec importerSpec) {
    return processSourceDatas(importerSpec)
        .stream()
        .map(this::getSourceAnalysisFiles)
        .flatMap(Collection::stream)
        .collect(toUnmodifiableList());
  }

  private List<FileDTO> getSourceAnalysisFiles(SourceData d) {
    return api.getAnalysisFiles(d.getStudyId(), d.getAnalysisId());
  }

  private List<SourceData> processSourceDatas(ImporterSpec importerSpec) {
    return importerSpec.getAnalysisIds().stream()
        .map(a -> {
          val studyId = getStudyForAnalysisId(a);
          return SourceData.builder().analysisId(a).studyId(studyId).build();
        })
        .collect(toUnmodifiableList());
  }

  @SneakyThrows
  public String getStudyForAnalysisId(@NonNull String analysisId) {
    val response = get(getLegacyEntityUrl(analysisId));
    val j = mapper().readTree(response.getBody());
    val contentNode = parseContent(j);
    checkImporter(
        !contentNode.isEmpty(),
        "The analysisId '%s' does not exist at '%s'",
        analysisId,
        config.getServerUrl());
    return contentNode.path(0).path("projectCode").asText();
  }

  private String getLegacyEntityUrl(String analysisId) {
    return format("%s/entities?gnosId=%s", config.getServerUrl(), analysisId);
  }

  public static String parseAnalysisId(Path file) {
    checkFileNameFormat(file);
    return file.getFileName().toString().replaceAll("\\.json$", "");
  }

  private static void checkFileNameFormat(Path targetPayloadFile) {
    checkImporter(
        targetPayloadFile.toString().endsWith(".json"),
        "The file '%s' is not a json file",
        targetPayloadFile.toString());
  }

  private static JsonNode parseContent(JsonNode root) {
    checkRequiredField(root, "content");
    return root.path("content");
  }
}
