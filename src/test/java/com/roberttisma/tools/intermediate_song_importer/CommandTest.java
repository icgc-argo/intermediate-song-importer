package com.roberttisma.tools.intermediate_song_importer;

import com.roberttisma.tools.intermediate_song_importer.cli.IntermediateSongImporterCommand;
import org.junit.Test;
import picocli.CommandLine;

public class CommandTest {

  @Test
  public void testCommand() {
    //    run("config get -p test");
    //    run("run -p test --threads 4 -o output.report.json -d
    // docker/intermediate-song-importer-data");
  }

  private void run(String command) {
    new CommandLine(new IntermediateSongImporterCommand()).execute(command.split("\\s+"));
  }
}
