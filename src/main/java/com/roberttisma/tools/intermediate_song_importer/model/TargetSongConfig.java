package com.roberttisma.tools.intermediate_song_importer.model;

import static com.roberttisma.tools.intermediate_song_importer.util.Fields.mergeField;
import static com.roberttisma.tools.intermediate_song_importer.util.Fields.mergeMergableField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TargetSongConfig implements Mergable<TargetSongConfig>, SongConfig {
  private String serverUrl;
  private String accessToken;

  @Builder.Default private DBConfig db = new DBConfig();

  @Override
  public void merge(@NonNull TargetSongConfig targetSongConfig) {
    mergeField(
        TargetSongConfig::getServerUrl, TargetSongConfig::setServerUrl, this, targetSongConfig);
    mergeField(
        TargetSongConfig::getAccessToken, TargetSongConfig::setAccessToken, this, targetSongConfig);
    mergeMergableField(TargetSongConfig::getDb, this, targetSongConfig);
  }
}
