package com.roberttisma.tools.intermediate_song_importer.util;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.function.BiConsumer;
import java.util.function.Function;
import lombok.val;

public class Fields {

  public static <T> void mergeField(
      Function<T, String> getter, BiConsumer<T, String> setter, T base, T input) {
    val value = getter.apply(input);

    if (!isNullOrEmpty(value)) {
      setter.accept(base, value);
    }
  }
}
