package com.roberttisma.tools.intermediate_song_importer.model.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.Path;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisTypeValidationErrorReport implements Report {

  private AnalysisTypeErrorType errorType;
  private String name;
  private int latestVersion;
  private int currentVersion;
  private List<Path> payloadFilenames;

  @Override public boolean hasErrors() {
    return true;
  }
}
