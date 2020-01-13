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
  private String accessToken;
  private String sourceUrl;
  private SongConfig targetSong;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class SongConfig implements Mergable<SongConfig> {
    private String serverUrl;
    private DBConfig db;

    @Override
    public void merge(@NonNull SongConfig songConfig) {
      mergeField(SongConfig::getServerUrl, SongConfig::setServerUrl, this, songConfig);
      mergeMergableField(SongConfig::getDb, this, songConfig);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DBConfig implements Mergable<DBConfig> {
      private String dbname;
      private String hostname;
      private String port = "5432";
      private String password;

      @Override
      public void merge(@NonNull DBConfig dbConfig) {
        mergeField(DBConfig::getDbname, DBConfig::setDbname, this, dbConfig);
        mergeField(DBConfig::getHostname, DBConfig::setHostname, this, dbConfig);
        mergeField(DBConfig::getPassword, DBConfig::setPassword, this, dbConfig);
        mergeField(DBConfig::getPort, DBConfig::setPort, this, dbConfig);
      }
    }
  }

  @Override
  public void merge(@NonNull ProfileConfig mergeIn) {
    mergeProfileField(ProfileConfig::getName, ProfileConfig::setName, mergeIn);
    mergeProfileField(ProfileConfig::getAccessToken, ProfileConfig::setAccessToken, mergeIn);
    mergeProfileField(ProfileConfig::getSourceUrl, ProfileConfig::setSourceUrl, mergeIn);
    mergeMergableField(ProfileConfig::getTargetSong, this, mergeIn);
  }

  private void mergeProfileField(
      Function<ProfileConfig, String> getter,
      BiConsumer<ProfileConfig, String> setter,
      ProfileConfig mergeIn) {
    mergeField(getter, setter, this, mergeIn);
  }
}
