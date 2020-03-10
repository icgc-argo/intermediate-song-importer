package com.roberttisma.tools.intermediate_song_importer;

import static com.roberttisma.tools.intermediate_song_importer.Factory.createRetry;
import static net.jodah.failsafe.Failsafe.with;

import com.zaxxer.hikari.HikariDataSource;
import java.io.Closeable;
import java.io.IOException;
import java.util.function.Function;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

@Slf4j
@RequiredArgsConstructor
public class Repository implements Closeable {

  @NonNull private final HikariDataSource hds;

  public int updateFileId(@NonNull String sourceObjectId, @NonNull String targetObjectId) {
    return execute(
        Integer.class,
        h ->
            h.createUpdate("UPDATE file SET id=:sid WHERE id=:tid")
                .bind("sid", sourceObjectId)
                .bind("tid", targetObjectId)
                .execute());
  }

  public int updateFileInfoId(@NonNull String sourceObjectId, @NonNull String targetObjectId) {
    return execute(
        Integer.class,
        h ->
            h.createUpdate("UPDATE info SET id=:sid WHERE id=:tid")
                .bind("sid", sourceObjectId)
                .bind("tid", targetObjectId)
                .execute());
  }

  // TODO: need to do json search for legacyAnalysisId in the file Info type
  //  public List<String> getFileIdsForLegacyAnalysisId(@NonNull String legacyAnalysisId){
  //    return execute(Integer.class, h -> {
  //      h.createQuery("SELECT id FROM info.info WHERE info.idtype = 'File'")
  //
  //    })
  //
  //  }

  @Override
  public void close() throws IOException {
    hds.close();
  }

  private Handle createHandle() {
    return Jdbi.create(hds).open();
  }

  private <R> R execute(Class<R> returnType, Function<Handle, R> fn) {
    return with(createRetry(returnType))
        .get(
            () -> {
              try (val handle = createHandle()) {
                return fn.apply(handle);
              }
            });
  }
}
