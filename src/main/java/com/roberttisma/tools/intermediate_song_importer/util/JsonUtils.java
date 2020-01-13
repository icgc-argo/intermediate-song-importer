package com.roberttisma.tools.intermediate_song_importer.util;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;

public class JsonUtils {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  public static class PrettyJsonPrinter extends DefaultPrettyPrinter {
    public static final PrettyJsonPrinter INSTANCE = new PrettyJsonPrinter(4);

    public PrettyJsonPrinter(int indentSize) {
      val sb = new StringBuilder();
      for (int i = 0; i < indentSize; i++) {
        sb.append(' ');
      }
      indentArraysWith(new DefaultIndenter(sb.toString(), DefaultIndenter.SYS_LF));
    }
  }

  public static ObjectMapper mapper() {
    return OBJECT_MAPPER;
  }

  @SneakyThrows
  public static String toPrettyJson(@NonNull Object o) {
    return mapper().writerWithDefaultPrettyPrinter().writeValueAsString(o);
  }
}
