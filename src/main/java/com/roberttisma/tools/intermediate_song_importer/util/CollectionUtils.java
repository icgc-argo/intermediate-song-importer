package com.roberttisma.tools.intermediate_song_importer.util;

import lombok.NonNull;

import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public class CollectionUtils {

  public static <T, R> List<R> mapToList(
      @NonNull List<T> values, @NonNull Function<T, R> function) {
    return values.stream().map(function).collect(toList());
  }
}
