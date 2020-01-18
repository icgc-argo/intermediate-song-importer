package com.roberttisma.tools.intermediate_song_importer.cli;

import com.roberttisma.tools.intermediate_song_importer.service.ProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.nio.file.Path;
import java.util.concurrent.Callable;

import static com.roberttisma.tools.intermediate_song_importer.util.FileIO.checkDirectoryExists;
import static com.roberttisma.tools.intermediate_song_importer.util.ProfileManager.findProfile;
import static java.lang.String.format;

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

  @Option(
      names = {"-t", "--threads"},
      description = "Number of threads to use. Default: ${DEFAULT-VALUE}",
      defaultValue = "1",
      required = false)
  private int numThreads;

  @Override
  public Integer call() throws Exception {
    checkDirectoryExists(inputDir);
    val result = findProfile(profileName);
    if (result.isPresent()) {
      val profileConfig = result.get();
      ProcessService.builder()
          .profileConfig(profileConfig)
          .inputDir(inputDir)
          .numThreads(numThreads)
          .build()
          .run();
    } else {
      val errorMessage = format("The profile '%s does not exist'", profileName);
      log.error(errorMessage);
      System.out.println(errorMessage);
      return 1;
    }
    return 0;
  }
}
