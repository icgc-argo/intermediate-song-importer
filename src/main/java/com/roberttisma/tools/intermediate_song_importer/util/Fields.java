package com.roberttisma.tools.intermediate_song_importer.util;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.isNull;

import com.roberttisma.tools.intermediate_song_importer.model.Mergable;
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

  public static <T, M extends Mergable<M>> void mergeMergableField(
      Function<T, M> getter, T base, T input) {
    val value = getter.apply(input);

    if (!isNull(value)) {
      getter.apply(base).merge(value);
    }
  }
}
