package com.roberttisma.tools.intermediate_song_importer.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static com.roberttisma.tools.intermediate_song_importer.exceptions.ImporterException.checkImporter;
import static java.util.Optional.ofNullable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImporterSpec {

  private static final String STUDY_ID = "studyId";

  private List<String> analysisIds;
  private JsonNode payload;
  private Path file;

  public Optional<String> getStudyId(){
    return payload.has(STUDY_ID) ?
        ofNullable(payload.path(STUDY_ID).asText()) : Optional.empty();
  }

}
