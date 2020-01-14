package com.roberttisma.tools.intermediate_song_importer.cli;

import static com.roberttisma.tools.intermediate_song_importer.util.ProfileManager.deleteProfile;

import com.roberttisma.tools.intermediate_song_importer.exceptions.ImporterException;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Slf4j
@RequiredArgsConstructor
@Command(name = "del", mixinStandardHelpOptions = true, description = "Deletes a profile")
public class ConfigDeleteCommand implements Callable<Integer> {

  @Option(
      names = {"-p", "--profile"},
      description = "Profile to delete",
      required = true)
  private String profileName;

  @Override
  public Integer call() throws Exception {
    try {
      deleteProfile(profileName);
      System.out.println("Deleted profile " + profileName);
    } catch (ImporterException e) {
      log.error(e.getMessage());
      System.err.println(e.getMessage());
      return 1;
    }
    return 0;
  }
}
