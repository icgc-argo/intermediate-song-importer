package com.roberttisma.tools.intermediate_song_importer;

import static java.lang.String.format;

import com.roberttisma.tools.intermediate_song_importer.model.ProfileConfig;
import lombok.val;
import org.jdbi.v3.core.Jdbi;

public class Factory {

  public static Jdbi createJdbi(ProfileConfig.SongConfig.DBConfig dbConfig) {
    val url =
        format(
            "jdbc:postgresql://%s:%s/%s?stringtype=unspecified",
            dbConfig.getHostname(), dbConfig.getPort(), dbConfig.getDbname());
    return Jdbi.create(url, dbConfig.getUsername(), dbConfig.getPassword());
  }
}
