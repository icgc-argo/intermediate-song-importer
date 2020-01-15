package com.roberttisma.tools.intermediate_song_importer;

import static java.lang.String.format;

import com.roberttisma.tools.intermediate_song_importer.model.DBConfig;
import java.io.Closeable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

@Slf4j
@RequiredArgsConstructor
public class DBUpdater implements Closeable {

  @NonNull private Handle handle;

  public int update(@NonNull String sourceObjectId, @NonNull String targetObjectId) {
    return handle
        .createUpdate("UPDATE file SET id=:sid WHERE id=:tid")
        .bind("sid", sourceObjectId)
        .bind("tid", targetObjectId)
        .execute();
  }

  @Override
  public void close() {
    handle.close();
  }

  public static DBUpdater createDBUpdater(@NonNull DBConfig dbConfig) {
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
