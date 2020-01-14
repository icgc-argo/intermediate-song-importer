package com.roberttisma.tools.intermediate_song_importer;

import com.roberttisma.tools.intermediate_song_importer.model.ProfileConfig.SongConfig.DBConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import java.io.Closeable;
import java.util.Map;

import static java.lang.String.format;

@RequiredArgsConstructor
public class DBUpdater implements Closeable {

  @NonNull private Handle handle;

  public void updateWithMap(@NonNull Map<String, String> objectIdMap) {
    objectIdMap.forEach(this::update);
  }

  public int update(@NonNull String sourceObjectId, @NonNull String targetObjectId) {
    return handle.createUpdate(
        "UPDATE file SET id=:targetObjectId WHERE id=:sourceObjectId")
        .bind("sourceObjectId", sourceObjectId)
        .bind("targetObjectId", targetObjectId)
        .execute();
  }

  @Override
  public void close() {
    handle.close();
  }

  public static DBUpdater createDBUpdater(@NonNull DBConfig dbConfig){
    return new DBUpdater(createJdbi(dbConfig).open());
  }

  private static String createUrl(DBConfig dbConfig) {
    return format(
        "jdbc:postgresql://%s:%s/%s?stringtype=unspecified",
        dbConfig.getHostname(), dbConfig.getPort(), dbConfig.getDbname());
  }

  private static Jdbi createJdbi(DBConfig dbConfig) {
    return Jdbi.create(createUrl(dbConfig), dbConfig.getUsername(), dbConfig.getPassword());
  }

}
