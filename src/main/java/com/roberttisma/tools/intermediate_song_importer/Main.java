package com.roberttisma.tools.intermediate_song_importer;

import com.roberttisma.tools.intermediate_song_importer.cli.IntermediateSongImporterCommand;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

@Slf4j
public class Main {

  public static void main(String[] args) throws IOException {
    new CommandLine(new IntermediateSongImporterCommand()).execute(args);
    //    new CommandLine(new IntermediateSongImporterCommand())
    //        .execute("config set --profile hi -s sdfsdf -t sdfsdf".split("\\s+"));
  }
}
