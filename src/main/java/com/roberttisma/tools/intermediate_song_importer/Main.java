package com.roberttisma.tools.intermediate_song_importer;

import com.roberttisma.tools.intermediate_song_importer.cli.ConfigCommand;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.io.IOException;

@Slf4j
public class Main {

  public static void main(String[] args) throws IOException {
    new CommandLine(new ConfigCommand()).execute(args);
  }
}
