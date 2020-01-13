package com.roberttisma.tools.intermediate_song_importer.cli;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;

@Slf4j
@RequiredArgsConstructor
@Command(
    name = "config",
    mixinStandardHelpOptions = true,
    subcommands = {ConfigSetCommand.class, ConfigGetCommand.class},
    description = "Configures the tool")
public class ConfigCommand {
  /*
  script config set --profile <>  --access-token <> --target-url <> --source-url <>
  script config get --profile <>
  script run --profile <> --study-id <> --output report.txt
   */
}
