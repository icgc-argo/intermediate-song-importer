package com.roberttisma.tools.intermediate_song_importer.service;

import bio.overture.song.core.model.Analysis;
import bio.overture.song.sdk.SongApi;
import com.roberttisma.tools.intermediate_song_importer.model.SongConfig;
import com.roberttisma.tools.intermediate_song_importer.model.Study;
import kong.unirest.HttpResponse;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.val;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import static com.roberttisma.tools.intermediate_song_importer.exceptions.ImporterException.buildImporterException;
import static com.roberttisma.tools.intermediate_song_importer.exceptions.ImporterException.checkImporter;
import static com.roberttisma.tools.intermediate_song_importer.util.FileIO.readFileContent;
import static com.roberttisma.tools.intermediate_song_importer.util.JsonUtils.mapper;
import static com.roberttisma.tools.intermediate_song_importer.util.RestClient.get;
import static com.roberttisma.tools.intermediate_song_importer.util.RestClient.post;
import static java.lang.String.format;

@Value
@Builder
public class TargetSongService {

  @NonNull private SongConfig config;
  @NonNull private SongApi api;

  public Analysis submitTargetPayload(@NonNull Path payloadFile) throws IOException {
    val targetPayload = readFileContent(payloadFile);
    val targetStudyId = extractStudyId(targetPayload);

    // Create the study if it does not exist
    if (!isStudyExist(targetStudyId)) {
      createStudy(targetStudyId);
    }

    val targetAnalysisId = api.submit(targetStudyId, targetPayload).getAnalysisId();
    return api.getAnalysis(targetStudyId, targetAnalysisId);
  }

  public void publishTargetAnalysis(Analysis a) {
    api.publish(a.getStudyId(), a.getAnalysisId(), false);
  }

  private boolean isStudyExist(@NonNull String studyId) {
    val response = get(getIsStudyExistUrl(studyId));
    return handleNotFound(
        response,
        "Error getting the studyId '%s' for host '%s': %s",
        studyId,
        config.getServerUrl(),
        response)
        .isPresent();
  }

  private void createStudy(@NonNull String studyId) {
    val body = Study.builder().studyId(studyId).build();
    val response = post(config.getAccessToken(), getCreateStudyUrl(studyId), body);
    checkImporter(
        response.isSuccess(),
        "Error creating studyId '%s': %s -> %s",
        studyId,
        response.getStatusText(),
        response.getBody());
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

  @SneakyThrows
  private static String extractStudyId(String payload) {
    val j= mapper().readTree(payload);
    checkImporter(j.has("studyId"), "json missing the studyId field");
    return j.path("studyId").asText();
  }

}
