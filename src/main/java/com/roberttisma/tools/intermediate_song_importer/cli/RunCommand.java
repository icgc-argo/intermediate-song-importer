package com.roberttisma.tools.intermediate_song_importer.cli;

import static com.roberttisma.tools.intermediate_song_importer.util.FileIO.checkDirectoryExists;
import static com.roberttisma.tools.intermediate_song_importer.util.ProfileManager.findProfile;
import static java.lang.String.format;

import com.roberttisma.tools.intermediate_song_importer.model.ProfileConfig;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Slf4j
@RequiredArgsConstructor
@Command(
    name = "run",
    mixinStandardHelpOptions = true,
    description = "Runs an import for a specific profile")
public class RunCommand implements Callable<Integer> {

  @Option(
      names = {"-p", "--profile"},
      description = "Profile to set",
      required = true)
  private String profileName;

  @Option(
      names = {"-d", "--input-dir"},
      description = "Directory containing input files with the format: <analysisId>.json",
      required = true)
  private Path inputDir;

  @Override
  public Integer call() throws Exception {
    checkDirectoryExists(inputDir);
    val result = findProfile(profileName);
    if (result.isPresent()) {
      val profileConfig = result.get();

    } else {
      val errorMessage = format("The profile '%s does not exist'", result);
      log.error(errorMessage);
      System.out.println(errorMessage);
      return 1;
    }
    return 0;
  }

  private void run(ProfileConfig config) {
    // get analys

  }
}
