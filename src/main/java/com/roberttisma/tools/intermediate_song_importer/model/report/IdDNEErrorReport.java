package com.roberttisma.tools.intermediate_song_importer.model.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdDNEErrorReport implements Report {

  private String errorType;
  private String entityType;
  private String studyId;
  private String submitterId;

  @Override
  public boolean hasErrors() {
    return true;
  }
}
