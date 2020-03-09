package com.roberttisma.tools.intermediate_song_importer;

import static bio.overture.song.sdk.Toolbox.createToolbox;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.time.temporal.ChronoUnit.SECONDS;

import bio.overture.song.sdk.SongApi;
import bio.overture.song.sdk.config.impl.DefaultRestClientConfig;
import com.roberttisma.tools.intermediate_song_importer.model.DBConfig;
import com.roberttisma.tools.intermediate_song_importer.model.SongConfig;
import com.roberttisma.tools.intermediate_song_importer.model.SourceSongConfig;
import com.roberttisma.tools.intermediate_song_importer.model.TargetSongConfig;
import com.roberttisma.tools.intermediate_song_importer.service.MigrationService;
import com.roberttisma.tools.intermediate_song_importer.service.SourceSongService;
import com.roberttisma.tools.intermediate_song_importer.service.TargetSongService;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.jodah.failsafe.RetryPolicy;

@Slf4j
public class Factory {

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

  private static SongApi createSongApi(@NonNull SongConfig c) {
    val accessToken = c.getAccessToken();
    val serverUrl = c.getServerUrl();
    checkNotNull(accessToken, "The accessToken field cannot be null for the Song api");
    return createToolbox(
            DefaultRestClientConfig.builder().accessToken(accessToken).serverUrl(serverUrl).build())
        .getSongApi();
  }

  private static SourceSongService createSourceSongService(@NonNull SourceSongConfig c) {
    return SourceSongService.builder().api(createSongApi(c)).config(c).build();
  }

  private static TargetSongService createTargetSongService(@NonNull TargetSongConfig c) {
    return TargetSongService.builder().api(createSongApi(c)).config(c).build();
  }
}
