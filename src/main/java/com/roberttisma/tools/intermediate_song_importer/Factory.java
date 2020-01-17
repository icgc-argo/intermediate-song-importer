package com.roberttisma.tools.intermediate_song_importer;

import static bio.overture.song.sdk.Toolbox.createToolbox;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.roberttisma.tools.intermediate_song_importer.DBUpdater.createDBUpdater;

import bio.overture.song.sdk.SongApi;
import bio.overture.song.sdk.config.impl.DefaultRestClientConfig;
import com.roberttisma.tools.intermediate_song_importer.model.ProfileConfig;
import com.roberttisma.tools.intermediate_song_importer.model.SongConfig;
import com.roberttisma.tools.intermediate_song_importer.service.MigrationService;
import com.roberttisma.tools.intermediate_song_importer.service.StudyService;
import lombok.NonNull;
import lombok.val;

public class Factory {

  public static MigrationService createMigrationService(@NonNull ProfileConfig c) {
    return MigrationService.builder()
        .dbUpdater(createDBUpdater(c.getTargetSong().getDb()))
        .sourceApi(createSourceSongApi(c))
        .targetApi(createTargetSongApi(c))
        .sourceStudyService(createSourceStudyService(c))
        .targetStudyService(createTargetStudyService(c))
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

  private static StudyService createSourceStudyService(@NonNull ProfileConfig c) {
    return StudyService.builder().config(c.getSourceSong()).build();
  }

  private static StudyService createTargetStudyService(@NonNull ProfileConfig c) {
    return StudyService.builder().config(c.getTargetSong()).build();
  }

  private static SongApi createSourceSongApi(@NonNull ProfileConfig c) {
    return createSongApi(c.getSourceSong());
  }

  private static SongApi createTargetSongApi(@NonNull ProfileConfig c) {
    return createSongApi(c.getTargetSong());
  }
}
