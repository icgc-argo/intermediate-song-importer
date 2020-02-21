package com.roberttisma.tools.intermediate_song_importer;

import static com.roberttisma.tools.intermediate_song_importer.Factory.createRetry;
import static net.jodah.failsafe.Failsafe.with;

import com.zaxxer.hikari.HikariDataSource;
import java.io.Closeable;
import java.io.IOException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

@Slf4j
@RequiredArgsConstructor
public class DBUpdater implements Closeable {

  @NonNull private final HikariDataSource hds;

  public int update(@NonNull String sourceObjectId, @NonNull String targetObjectId) {
    return with(createRetry(Integer.class))
        .get(
            () -> {
              try (val handle = createHandle()) {
                return handle
                    .createUpdate("UPDATE file SET id=:sid WHERE id=:tid")
                    .bind("sid", sourceObjectId)
                    .bind("tid", targetObjectId)
                    .execute();
              }
            });
  }

  @Override
  public void close() throws IOException {
    hds.close();
  }

  private Handle createHandle() {
    return Jdbi.create(hds).open();
  }
}
