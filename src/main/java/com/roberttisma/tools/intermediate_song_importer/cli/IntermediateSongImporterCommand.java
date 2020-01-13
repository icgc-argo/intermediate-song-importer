package com.roberttisma.tools.intermediate_song_importer.cli;

import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Slf4j
@RequiredArgsConstructor
@Command(
    name = "intermediate-song-importer",
    subcommands = {ConfigCommand.class, RunCommand.class},
    description = "Main command")
public class IntermediateSongImporterCommand implements Callable<Integer> {

  @Override
  public Integer call() throws Exception {
    CommandLine.usage(this, System.out);
    return 0;
  }
}
