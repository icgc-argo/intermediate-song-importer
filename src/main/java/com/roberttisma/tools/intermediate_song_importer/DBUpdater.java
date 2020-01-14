package com.roberttisma.tools.intermediate_song_importer;

import static java.lang.String.format;

import com.roberttisma.tools.intermediate_song_importer.model.ProfileConfig;
import java.util.Map;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

@Value
@Builder
public class DBUpdater {

  @NonNull private final ProfileConfig.SongConfig.DBConfig dbConfig;
  @NonNull private final Map<String, String> objectIdMap;

  public void updateObjectIds() {
    createJdbi(dbConfig).useHandle(this::updateAll);
  }

  private void updateAll(Handle handle) {
    objectIdMap.forEach((s, t) -> update(handle, s, t));
  }

  private static String createUrl(ProfileConfig.SongConfig.DBConfig dbConfig) {
    return format(
        "jdbc:postgresql://%s:%s/%s?stringtype=unspecified",
        dbConfig.getHostname(), dbConfig.getPort(), dbConfig.getDbname());
  }

  private static Jdbi createJdbi(ProfileConfig.SongConfig.DBConfig dbConfig) {
    return Jdbi.create(createUrl(dbConfig), dbConfig.getUsername(), dbConfig.getPassword());
  }

  private static int update(Handle h, String sourceObjectId, String targetObjectId) {
    return h.createUpdate(
            "UPDATE file SET objectid=:targetObjectId WHERE file.objectId=:sourceObjectId")
        .bind("sourceObjectId", sourceObjectId)
        .bind("targetObjectId", targetObjectId)
        .execute();
  }
}
