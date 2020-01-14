package com.roberttisma.tools.intermediate_song_importer;

import com.roberttisma.tools.intermediate_song_importer.cli.IntermediateSongImporterCommand;
import org.junit.Test;
import picocli.CommandLine;

public class CommandTest {

  @Test
  public void testrob() {
    //    run("config get -l");
//    run("run -p rob -d ./");
    run("run -p devtest -d /home/rtisma/Downloads/testpayloads");
  }

  private void run(String command) {
    new CommandLine(new IntermediateSongImporterCommand()).execute(command.split("\\s+"));
  }
}
