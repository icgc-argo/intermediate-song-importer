package com.roberttisma.tools.intermediate_song_importer;

import static bio.overture.song.sdk.Toolbox.createToolbox;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.roberttisma.tools.intermediate_song_importer.service.id.UriResolver.createUriResolver;
import static java.time.temporal.ChronoUnit.SECONDS;

import bio.overture.song.sdk.SongApi;
import bio.overture.song.sdk.config.impl.DefaultRestClientConfig;
import com.roberttisma.tools.intermediate_song_importer.model.DBConfig;
import com.roberttisma.tools.intermediate_song_importer.model.IdConfig;
import com.roberttisma.tools.intermediate_song_importer.model.SongConfig;
import com.roberttisma.tools.intermediate_song_importer.model.SourceSongConfig;
import com.roberttisma.tools.intermediate_song_importer.model.TargetSongConfig;
import com.roberttisma.tools.intermediate_song_importer.service.AnalysisTypeValidationService;
import com.roberttisma.tools.intermediate_song_importer.service.IdValidationService;
import com.roberttisma.tools.intermediate_song_importer.service.MigrationService;
import com.roberttisma.tools.intermediate_song_importer.service.SourceSongService;
import com.roberttisma.tools.intermediate_song_importer.service.TargetSongService;
import com.roberttisma.tools.intermediate_song_importer.service.id.FederatedIdService;
import com.roberttisma.tools.intermediate_song_importer.service.id.IdService;
import com.roberttisma.tools.intermediate_song_importer.service.id.UriResolver;
import com.roberttisma.tools.intermediate_song_importer.util.RestClient;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import kong.unirest.HttpResponse;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.jodah.failsafe.RetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Slf4j
public class Factory {

  public static AnalysisTypeValidationService createAnalysisTypeValidationService(@NonNull TargetSongConfig targetSongConfig){
    val song = createTargetSongService(targetSongConfig);
    return new AnalysisTypeValidationService(song);
  }

  public static IdValidationService createIdValidationService(@NonNull IdConfig idConfig){
    val idService = createIdService(idConfig);
    return new IdValidationService(idService);
  }

  public static <T> RetryPolicy<T> createRetry(@NonNull Class<T> type) {
    return new RetryPolicy<T>()
        .withMaxRetries(3)
        .onFailedAttempt(
            executionAttemptedEvent -> {
              val message = executionAttemptedEvent.getLastFailure().getMessage();
              val type1 = executionAttemptedEvent.getLastFailure().getClass().getSimpleName();
              log.error("Failed attempt -> [{}]: {}", type1, message);
            })
        .onRetry(
            executionAttemptedEvent ->
                log.warn("Failure # {}. Retrying", executionAttemptedEvent.getAttemptCount()))
        .withBackoff(1, 30, SECONDS);
  }

  public static MigrationService createMigrationService(
      @NonNull SourceSongConfig sc, @NonNull TargetSongConfig tc, @NonNull Repository repository) {
    return MigrationService.builder()
        .repository(repository)
        .targetSongService(createTargetSongService(tc))
        .sourceSongService(createSourceSongService(sc))
        .build();
  }

  @SneakyThrows
  public static Repository createRepository(@NonNull DBConfig dbConfig) {
    val c = new HikariConfig();
    c.setJdbcUrl(dbConfig.createUrl());
    c.setUsername(dbConfig.getUsername());
    c.setPassword(dbConfig.getPassword());
    return new Repository(new HikariDataSource(c));
  }

  private static IdService createIdService(@NonNull IdConfig idConfig){
    val restClient = createRestClient();
    val uriResolver = createUriResolver(idConfig);
    return new FederatedIdService(restClient, uriResolver );
  }

  private static RestClient createRestClient(){
    val retry = createRetry(HttpResponse.class);
    return new RestClient(retry);
  }

  private static SongApi createSongApi(@NonNull SongConfig c) {
    val accessToken = c.getAccessToken();
    val serverUrl = c.getServerUrl();
    checkNotNull(accessToken, "The accessToken field cannot be null for the Song api");
    return createToolbox(
            DefaultRestClientConfig.builder().accessToken(accessToken).serverUrl(serverUrl).build())
        .getSongApi();
  }

  private static SourceSongService createSourceSongService(@NonNull SourceSongConfig c) {
    return SourceSongService.builder()
        .restClient(createRestClient())
        .api(createSongApi(c))
        .config(c)
        .build();
  }

  private static TargetSongService createTargetSongService(@NonNull TargetSongConfig c) {
    return TargetSongService.builder()
        .restClient(createRestClient())
        .api(createSongApi(c))
        .config(c).build();
  }
}
