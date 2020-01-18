package com.roberttisma.tools.intermediate_song_importer.util;

import static com.roberttisma.tools.intermediate_song_importer.util.JsonUtils.checkRequiredField;
import static com.roberttisma.tools.intermediate_song_importer.util.JsonUtils.readTree;

import java.nio.file.Path;
import lombok.val;

public class Payloads {

  private static final String STUDY_ID = "studyId";

  public static String parseStudyId(Path jsonPath) {
    val root = readTree(jsonPath);
    checkRequiredField(root, STUDY_ID);
    return root.path(STUDY_ID).asText();
  }
}
