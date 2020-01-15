package com.roberttisma.tools.intermediate_song_importer.cli;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.roberttisma.tools.intermediate_song_importer.util.ProfileManager.saveProfile;

import com.roberttisma.tools.intermediate_song_importer.model.ProfileConfig;
import java.util.concurrent.Callable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Getter
@RequiredArgsConstructor
@Command(
    name = "set",
    subcommands = {ConfigSetSourceCommand.class, ConfigSetTargetCommand.class},
    mixinStandardHelpOptions = true,
    description = "Sets a profiles configuration")
public class ConfigSetCommand implements Callable<Integer> {

  @Option(
      names = {"-p", "--profile"},
      description = "Profile to set",
      required = false)
  private String profileName;

  @Override
  public Integer call() throws Exception {
    if (isNullOrEmpty(profileName)) {
      CommandLine.usage(this, System.out);
    } else {
      val profileConfig = ProfileConfig.builder().name(profileName).build();
      val status = saveProfile(profileConfig);
      System.out.println(status);
    }
    return 0;
  }
}
