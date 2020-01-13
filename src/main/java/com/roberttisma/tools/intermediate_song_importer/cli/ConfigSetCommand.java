package com.roberttisma.tools.intermediate_song_importer.cli;

import com.roberttisma.tools.intermediate_song_importer.model.ProfileConfig;
import lombok.RequiredArgsConstructor;
import lombok.val;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

import static com.roberttisma.tools.intermediate_song_importer.util.ProfileManager.saveProfile;

@RequiredArgsConstructor
@Command(name = "set", mixinStandardHelpOptions = true, description = "Sets a profiles configuration")
public class ConfigSetCommand implements Callable<Integer> {

  @CommandLine.Option(
      names = {"-p", "--profile"},
      description = "Profile to set",
      required = true)
  private String profileName;

  @CommandLine.Option(
      names = {"-a", "--access-token"},
      description = "Set the access token for the intermediate song",
      required = false)
  private String accessToken;

  @CommandLine.Option(
      names = {"-t", "--target-url"},
      description = "Set target url for the intermediate song",
      required = false)
  private String targetUrl;

  @CommandLine.Option(
      names = {"-s", "--source-url"},
      description = "Set source url that will be imported into the intermediate song",
      required = false)
  private String sourceUrl;

  @Override
  public Integer call() throws Exception {
    val profileConfig =
        ProfileConfig.builder()
            .accessToken(accessToken)
            .name(profileName)
            .sourceUrl(sourceUrl)
            .targetUrl(targetUrl)
            .build();
    saveProfile(profileConfig);
    return 0;
  }
}
