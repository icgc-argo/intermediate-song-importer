package com.roberttisma.tools.intermediate_song_importer.model;

import static com.roberttisma.tools.intermediate_song_importer.util.Fields.mergeField;
import static java.lang.String.format;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DBConfig implements Mergable<DBConfig> {
  private String dbname;
  private String hostname;
  private String port;
  private String username;
  private String password;

  public String createUrl() {
    return format(
        "jdbc:postgresql://%s:%s/%s?stringtype=unspecified", getHostname(), getPort(), getDbname());
  }

  @Override
  public void merge(@NonNull DBConfig dbConfig) {
    mergeField(DBConfig::getDbname, DBConfig::setDbname, this, dbConfig);
    mergeField(DBConfig::getHostname, DBConfig::setHostname, this, dbConfig);
    mergeField(DBConfig::getUsername, DBConfig::setUsername, this, dbConfig);
    mergeField(DBConfig::getPassword, DBConfig::setPassword, this, dbConfig);
    mergeField(DBConfig::getPort, DBConfig::setPort, this, dbConfig);
  }
}
