package com.roberttisma.tools.intermediate_song_importer.model;

import static com.roberttisma.tools.intermediate_song_importer.util.Fields.mergeField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SourceSongConfig implements Mergable<SourceSongConfig>, SongConfig {
  private String serverUrl;
  private String accessToken;

  @Override
  public void merge(@NonNull SourceSongConfig targetSongConfig) {
    mergeField(
        SourceSongConfig::getServerUrl, SourceSongConfig::setServerUrl, this, targetSongConfig);
    mergeField(
        SourceSongConfig::getAccessToken, SourceSongConfig::setAccessToken, this, targetSongConfig);
  }
}
