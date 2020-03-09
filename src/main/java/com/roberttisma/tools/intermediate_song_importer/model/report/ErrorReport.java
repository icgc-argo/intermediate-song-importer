package com.roberttisma.tools.intermediate_song_importer.model.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorReport implements Report {

  private String payloadFilename;
  private String errorType;
  private String message;

  @Override
  public boolean hasErrors() {
    return true;
  }
}
