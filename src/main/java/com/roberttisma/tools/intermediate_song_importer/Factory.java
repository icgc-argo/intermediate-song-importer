package com.roberttisma.tools.intermediate_song_importer;

import bio.overture.song.sdk.SongApi;
import bio.overture.song.sdk.config.impl.DefaultRestClientConfig;
import com.roberttisma.tools.intermediate_song_importer.model.ProfileConfig;
import com.roberttisma.tools.intermediate_song_importer.model.SongConfig;
import com.roberttisma.tools.intermediate_song_importer.service.MigrationService;
import com.roberttisma.tools.intermediate_song_importer.service.SourceSongService;
import com.roberttisma.tools.intermediate_song_importer.service.TargetSongService;
import lombok.NonNull;
import lombok.val;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.roberttisma.tools.intermediate_song_importer.DBUpdater.createDBUpdater;
import static bio.overture.song.sdk.Toolbox.createToolbox;

public class Factory {

  public static MigrationService createMigrationService(@NonNull ProfileConfig c) {
    return MigrationService.builder()
        .dbUpdater(createDBUpdater(c.getTargetSong().getDb()))
        .targetSongService(createTargetSongService(c))
        .sourceSongService(createSourceSongService(c))
        .build();
  }

  private static SongApi createSongApi(@NonNull SongConfig c) {
    val accessToken = c.getAccessToken();
    val serverUrl = c.getServerUrl();
    checkNotNull(accessToken, "The accessToken field cannot be null for the Song api");
    return createToolbox(
            DefaultRestClientConfig.builder().accessToken(accessToken).serverUrl(serverUrl).build())
        .getSongApi();
  }

  private static SourceSongService createSourceSongService(@NonNull ProfileConfig c) {
    return SourceSongService.builder()
        .api(createSongApi(c.getSourceSong()))
        .config(c.getSourceSong())
        .build();
  }

  private static TargetSongService createTargetSongService(@NonNull ProfileConfig c) {
    return TargetSongService.builder()
        .api(createSongApi(c.getTargetSong()))
        .config(c.getTargetSong())
        .build();
  }

}
