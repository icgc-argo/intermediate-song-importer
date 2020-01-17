package com.roberttisma.tools.intermediate_song_importer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.roberttisma.tools.intermediate_song_importer.model.SongConfig;
import com.roberttisma.tools.intermediate_song_importer.model.Study;
import kong.unirest.HttpResponse;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.val;

import java.util.Optional;

import static com.roberttisma.tools.intermediate_song_importer.exceptions.ImporterException.buildImporterException;
import static com.roberttisma.tools.intermediate_song_importer.exceptions.ImporterException.checkImporter;
import static com.roberttisma.tools.intermediate_song_importer.util.JsonUtils.mapper;
import static com.roberttisma.tools.intermediate_song_importer.util.RestClient.get;
import static com.roberttisma.tools.intermediate_song_importer.util.RestClient.post;
import static java.lang.String.format;

@Value
@Builder
public class StudyService {

  @NonNull private SongConfig config;

  public boolean isStudyExist(@NonNull String studyId) {
    val response = get(getIsStudyExistUrl(studyId));
    return handleNotFound(
            response,
            "Error getting the studyId '%s' for host '%s': %s",
            studyId,
            config.getServerUrl(),
            response)
        .isPresent();
  }

  public void createStudy(@NonNull String studyId) {
    val body = Study.builder().studyId(studyId).build();
    val response = post(config.getAccessToken(), getCreateStudyUrl(studyId), body);
    checkImporter(
        response.isSuccess(),
        "Error creating studyId '%s': %s -> %s",
        studyId,
        response.getStatusText(),
        response.getBody());
  }

  @SneakyThrows
  public String getStudyForAnalysisId(@NonNull String analysisId) {
    val response = get(getLegacyEntityUrl(analysisId));
    val j = mapper().readTree(response.getBody());
    val contentNode = parseContent(j);
    checkImporter(!contentNode.isEmpty(),
        "The analysisId '%s' does not exist at '%s'",
        analysisId, getConfig().getServerUrl());
    return contentNode.path(0).path("projectCode").asText();
  }

  private static JsonNode parseContent(JsonNode root){
    checkRequiredField(root, "content");
    return root.path("content");
  }

  private static void checkRequiredField(JsonNode j, String field){
    checkImporter(j.has(field), "Could not find field '%s' in %", field, j.toString());
  }

  private String getLegacyEntityUrl(String analysisId) {
    return format("%s/entities?gnosId=%s", config.getServerUrl(), analysisId);
  }

  private String getIsStudyExistUrl(String studyId) {
    return format("%s/studies/%s", config.getServerUrl(), studyId);
  }

  private String getCreateStudyUrl(String studyId) {
    return format("%s/studies/%s/", config.getServerUrl(), studyId);
  }

  private static <T> Optional<T> handleNotFound(
      HttpResponse<T> response, String errorFormattedString, Object... args) {
    if (isNotFound(response.getStatus())) {
      return Optional.empty();
    } else if (!isError(response.getStatus())) {
      return Optional.of(response.getBody());
    } else {
      throw buildImporterException(errorFormattedString, args);
    }
  }

  private static boolean isNotFound(int statusCode) {
    return statusCode == 404;
  }

  private static boolean isError(int statusCode) {
    return statusCode >= 400;
  }
}
