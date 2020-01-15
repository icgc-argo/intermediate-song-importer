package com.roberttisma.tools.intermediate_song_importer.model;

import static com.roberttisma.tools.intermediate_song_importer.util.Fields.mergeField;
import static com.roberttisma.tools.intermediate_song_importer.util.Fields.mergeMergableField;

import java.util.function.BiConsumer;
import java.util.function.Function;
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
    mergeProfileField(ProfileConfig::getName, ProfileConfig::setName, mergeIn);
    mergeMergableField(ProfileConfig::getTargetSong, this, mergeIn);
    mergeMergableField(ProfileConfig::getSourceSong, this, mergeIn);
  }

  private void mergeProfileField(
      Function<ProfileConfig, String> getter,
      BiConsumer<ProfileConfig, String> setter,
      ProfileConfig mergeIn) {
    mergeField(getter, setter, this, mergeIn);
  }
}
