package com.roberttisma.tools.intermediate_song_importer.model.report;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuccessReport implements Report {

  private String payloadFilename;
  private String targetAnalysisId;
  private String targetStudyId;
  private String targetAnalysisState;
  private String legacyStudyId;
  @Builder.Default private Set<String> legacyAnalysisIds = newHashSet();
  @Builder.Default private Set<String> objectIdsMigrated = newHashSet();
  private boolean isAllObjectIdsMigrated;

  @Override
  public boolean hasErrors() {
    return false;
  }
}
