package com.roberttisma.tools.intermediate_song_importer.service.id;

import com.roberttisma.tools.intermediate_song_importer.util.RestClient;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.Optional;
import java.util.function.Function;

import static bio.overture.song.core.exceptions.ServerErrors.ID_SERVICE_ERROR;
import static bio.overture.song.core.exceptions.ServerException.buildServerException;

/** Implementation that calls an external service for ID federation */
@RequiredArgsConstructor
public class FederatedIdService implements IdService {

  /** Dependencies */
  @NonNull private final RestClient rest;

  @NonNull private final UriResolver uriResolver;

  @Override
  public Optional<String> getDonorId(@NonNull String studyId, @NonNull String submitterDonorId) {
    return handleIdServiceGetRequest(
        uriResolver.expandDonorUri(studyId, submitterDonorId), rest::getString);
  }

  @Override
  public Optional<String> getSpecimenId(
      @NonNull String studyId, @NonNull String submitterSpecimenId) {
    return handleIdServiceGetRequest(
        uriResolver.expandSpecimenUri(studyId, submitterSpecimenId), rest::getString);
  }

  @Override
  public Optional<String> getSampleId(@NonNull String studyId, @NonNull String submitterSampleId) {
    return handleIdServiceGetRequest(
        uriResolver.expandSampleUri(studyId, submitterSampleId), rest::getString);
  }

  /**
   * This method calls the callback function with the input url, and if successful (1xx/2xx/3xx
   * status code) returns the result, otherwise throws a ServerException
   */
  private static <T> T handleIdServiceGetRequest(String url, Function<String, T> restCallback) {
    try {
      return restCallback.apply(url);
    } catch (HttpStatusCodeException e) {
      val status = e.getStatusCode();
      val name = status.name();
      val code = status.value();
      throw buildServerException(
          FederatedIdService.class,
          ID_SERVICE_ERROR,
          "The request 'GET %s' failed with HttpStatus '%s[%s]' and message: %s",
          url,
          name,
          code,
          e.getMessage());
    }
  }
}
