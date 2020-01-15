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
public class ProfileConfig implements Mergable<ProfileConfig> {
  private String name;

  @Builder.Default private SourceSongConfig sourceSong = new SourceSongConfig();

  @Builder.Default private TargetSongConfig targetSong = new TargetSongConfig();

  @Override
  public void merge(@NonNull ProfileConfig mergeIn) {
    mergeField(ProfileConfig::getName, ProfileConfig::setName, this, mergeIn);
    mergeMergableField(ProfileConfig::getTargetSong, this, mergeIn);
    mergeMergableField(ProfileConfig::getSourceSong, this, mergeIn);
  }
}
