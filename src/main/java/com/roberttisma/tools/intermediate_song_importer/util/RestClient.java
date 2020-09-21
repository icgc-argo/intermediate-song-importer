package com.roberttisma.tools.intermediate_song_importer.util;

import static com.roberttisma.tools.intermediate_song_importer.Factory.createRetry;
import static com.roberttisma.tools.intermediate_song_importer.exceptions.ImporterException.buildImporterException;
import static kong.unirest.HeaderNames.AUTHORIZATION;
import static kong.unirest.HeaderNames.CONTENT_TYPE;
import static net.jodah.failsafe.Failsafe.with;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.roberttisma.tools.intermediate_song_importer.exceptions.ImporterException;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.jodah.failsafe.RetryPolicy;

import java.util.Optional;

@RequiredArgsConstructor
public class RestClient {

  @NonNull private final RetryPolicy<HttpResponse> retryPolicy;

  public Optional<String> getString(@NonNull String url) {
    val resp = get(url);
    if(resp.isSuccess()){
      return Optional.ofNullable(resp.getBody());
    }else if(resp.getStatus() == NOT_FOUND.value()){
      return Optional.empty();
    } else {
      throw buildImporterException("Unhandled error getting url: %s", url);
    }
  }

  /**
   * Executes a HTTP GET request for the url and deserializes the response to the type specified by
   * {@param responseType}
   */
  public HttpResponse<String> get(@NonNull String url) {
    return with(retryPolicy)
        .get(() -> internalGet(url));
  }

  public HttpResponse<String> get(@NonNull String accessToken, @NonNull String url) {
    return with(retryPolicy)
        .get(() -> internalGet(accessToken, url));
  }

  public <T> HttpResponse<String> post(
      @NonNull String accessToken, @NonNull String url, @NonNull T body) {
    return with(retryPolicy)
        .get(() -> internalPost(accessToken, url, body));
  }

  private HttpResponse<String> internalGet(@NonNull String accessToken, @NonNull String url) {
    return Unirest.get(url)
        .header(AUTHORIZATION, "Bearer " + accessToken)
        .header(CONTENT_TYPE, "application/json")
        .asString();
  }

  private  static HttpResponse<String> internalGet(@NonNull String url) {
    return Unirest.get(url).header(CONTENT_TYPE, "application/json").asString();
  }

  private <T> HttpResponse<String> internalPost(
      @NonNull String accessToken, @NonNull String url, @NonNull T body) {
    return Unirest.post(url)
        .header(AUTHORIZATION, "Bearer " + accessToken)
        .header(CONTENT_TYPE, "application/json")
        .body(body)
        .asString();
  }
}
