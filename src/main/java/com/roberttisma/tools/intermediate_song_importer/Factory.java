package com.roberttisma.tools.intermediate_song_importer;

import static bio.overture.song.sdk.Toolbox.createToolbox;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.roberttisma.tools.intermediate_song_importer.DBUpdater.createDBUpdater;

import bio.overture.song.sdk.SongApi;
import bio.overture.song.sdk.config.impl.DefaultRestClientConfig;
import com.roberttisma.tools.intermediate_song_importer.model.ProfileConfig;
import com.roberttisma.tools.intermediate_song_importer.service.MigrationService;
import lombok.NonNull;

public class Factory {

  public static MigrationService createMigrationService(@NonNull ProfileConfig c) {
    return MigrationService.builder()
        .dbUpdater(createDBUpdater(c.getTargetSong().getDb()))
        .sourceApi(createSourceSongApi(c))
        .targetApi(createTargetSongApi(c))
        .build();
  }

  private static SongApi createSongApi(@NonNull String serverUrl, String accessToken) {
    return createToolbox(
            DefaultRestClientConfig.builder().accessToken(accessToken).serverUrl(serverUrl).build())
        .getSongApi();
  }

  private static SongApi createSourceSongApi(@NonNull ProfileConfig c) {
    return createSongApi(c.getSourceUrl(), null);
  }

  private static SongApi createTargetSongApi(@NonNull ProfileConfig c) {
    checkNotNull(c.getAccessToken(), "The accessToken field cannot be null for the targetSong api");
    return createSongApi(c.getSourceUrl(), c.getAccessToken());
  }
}
