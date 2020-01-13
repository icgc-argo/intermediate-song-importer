package com.roberttisma.tools.intermediate_song_importer.cli;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;

@Slf4j
@RequiredArgsConstructor
@Command(
    name = "intermediate-song-importer",
    subcommands = {ConfigCommand.class},
    mixinStandardHelpOptions = true,
    description = "Main command")
public class MainCommand {}
