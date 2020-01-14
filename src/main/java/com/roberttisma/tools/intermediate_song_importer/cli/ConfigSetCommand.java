package com.roberttisma.tools.intermediate_song_importer.cli;

import static com.roberttisma.tools.intermediate_song_importer.util.ProfileManager.saveProfile;

import com.roberttisma.tools.intermediate_song_importer.model.ProfileConfig;
import com.roberttisma.tools.intermediate_song_importer.model.ProfileConfig.SongConfig;
import com.roberttisma.tools.intermediate_song_importer.model.ProfileConfig.SongConfig.DBConfig;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import lombok.val;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@RequiredArgsConstructor
@Command(
    name = "set",
    mixinStandardHelpOptions = true,
    description = "Sets a profiles configuration")
public class ConfigSetCommand implements Callable<Integer> {

  @Option(
      names = {"-p", "--profile"},
      description = "Profile to set",
      required = true)
  private String profileName;

  @Option(
      names = {"-a", "--access-token"},
      interactive = false,
      description = "Set the access token for the intermediate song",
      required = false)
  private String accessToken;

  @Option(
      names = {"-s", "--source-url"},
      description = "Set source url that will be imported into the intermediate song",
      required = false)
  private String sourceUrl;

  @Option(
      names = {"-t", "--target-url"},
      description = "Set target url of the intermediate song server",
      required = false)
  private String targetUrl;

  @Option(
      names = {"-dn", "--db-name"},
      description = "Set the target database name",
      defaultValue = "song",
      required = false)
  private String dbname;

  @Option(
      names = {"-dh", "--db-hostname"},
      description = "Set the target database hostname",
      defaultValue = "localhost",
      required = false)
  private String dbHostname;

  @Option(
      names = {"-dp", "--db-port"},
      description = "Set the target database port",
      defaultValue = "5432",
      required = false)
  private String dbPort;

  @Option(
      names = {"-du", "--db-username"},
      interactive = false,
      description = "Set the target database username",
      required = false)
  private String dbUsername;

  @Option(
      names = {"-dw", "--db-password"},
      interactive = false,
      description = "Set the target database password",
      required = false)
  private String dbPassword;

  @Override
  public Integer call() throws Exception {
    val profileConfig =
        ProfileConfig.builder()
            .accessToken(accessToken)
            .name(profileName)
            .sourceUrl(sourceUrl)
            .targetSong(
                SongConfig.builder()
                    .serverUrl(targetUrl)
                    .db(
                        DBConfig.builder()
                            .dbname(dbname)
                            .hostname(dbHostname)
                            .password(dbPassword)
                            .username(dbUsername)
                            .port(dbPort)
                            .build())
                    .build())
            .build();
    val status = saveProfile(profileConfig);
    System.out.println(status);
    return 0;
  }
}
