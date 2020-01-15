package com.roberttisma.tools.intermediate_song_importer.cli;

import static com.roberttisma.tools.intermediate_song_importer.util.ProfileManager.saveProfile;

import com.roberttisma.tools.intermediate_song_importer.model.DBConfig;
import com.roberttisma.tools.intermediate_song_importer.model.ProfileConfig;
import com.roberttisma.tools.intermediate_song_importer.model.TargetSongConfig;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Slf4j
@RequiredArgsConstructor
@Command(
    name = "target",
    mixinStandardHelpOptions = true,
    description = "Sets the target configuration for a profiles")
public class ConfigSetTargetCommand implements Callable<Integer> {

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
      names = {"-u", "--url"},
      description = "Set target url of the intermediate song",
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
            .name(profileName)
            .targetSong(
                TargetSongConfig.builder()
                    .accessToken(accessToken)
                    .serverUrl(targetUrl)
                    .db(
                        DBConfig.builder()
                            .dbname(dbname)
                            .hostname(dbHostname)
                            .password(dbPassword)
                            .port(dbPort)
                            .username(dbUsername)
                            .build())
                    .build())
            .build();
    val status = saveProfile(profileConfig);
    System.out.println(status);
    return 0;
  }
}
