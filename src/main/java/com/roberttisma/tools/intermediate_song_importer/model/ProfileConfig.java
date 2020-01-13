package com.roberttisma.tools.intermediate_song_importer.model;

import static com.roberttisma.tools.intermediate_song_importer.util.Fields.mergeField;

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
public class ProfileConfig {
  private String name;
  private String accessToken;
  private String sourceUrl;
  private String targetUrl;

  public void merge(@NonNull ProfileConfig mergeIn) {
    mergeProfileField(ProfileConfig::getName, ProfileConfig::setName, mergeIn);
    mergeProfileField(ProfileConfig::getAccessToken, ProfileConfig::setAccessToken, mergeIn);
    mergeProfileField(ProfileConfig::getSourceUrl, ProfileConfig::setSourceUrl, mergeIn);
    mergeProfileField(ProfileConfig::getTargetUrl, ProfileConfig::setTargetUrl, mergeIn);
  }

  private void mergeProfileField(
      Function<ProfileConfig, String> getter,
      BiConsumer<ProfileConfig, String> setter,
      ProfileConfig mergeIn) {
    mergeField(getter, setter, this, mergeIn);
  }
}
