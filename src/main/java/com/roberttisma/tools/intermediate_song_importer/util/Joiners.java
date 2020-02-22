package com.roberttisma.tools.intermediate_song_importer.util;

import com.google.common.base.Joiner;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class Joiners {

  public static final Joiner COMMA_SPACE = Joiner.on(", ");

}
