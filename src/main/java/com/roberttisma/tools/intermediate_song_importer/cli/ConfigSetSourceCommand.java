package com.roberttisma.tools.intermediate_song_importer.cli;

import static com.roberttisma.tools.intermediate_song_importer.util.ProfileManager.saveProfile;

import com.roberttisma.tools.intermediate_song_importer.model.ProfileConfig;
import com.roberttisma.tools.intermediate_song_importer.model.SourceSongConfig;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Slf4j
@RequiredArgsConstructor
@Command(
    name = "source",
    mixinStandardHelpOptions = true,
    description = "Sets the source configuration for a profiles")
public class ConfigSetSourceCommand implements Callable<Integer> {

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
      description = "Set source url that will be imported into the intermediate song",
      required = false)
  private String sourceUrl;

  @Override
  public Integer call() throws Exception {
    val profileConfig =
        ProfileConfig.builder()
            .name(profileName)
            .sourceSong(
                SourceSongConfig.builder().accessToken(accessToken).serverUrl(sourceUrl).build())
            .build();
    val status = saveProfile(profileConfig);
    System.out.println(status);
    return 0;
  }
}
