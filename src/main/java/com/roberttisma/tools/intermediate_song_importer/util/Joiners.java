package com.roberttisma.tools.intermediate_song_importer.util;

import static lombok.AccessLevel.PRIVATE;

import com.google.common.base.Joiner;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class Joiners {

  public static final Joiner COMMA_SPACE = Joiner.on(", ");
}
