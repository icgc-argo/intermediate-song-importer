package com.roberttisma.tools.intermediate_song_importer.model.report;

import kong.unirest.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.Path;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

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
    @Builder.Default  private Set<String> legacyAnalysisIds = newHashSet();
    @Builder.Default private Set<String> objectIdsMigrated = newHashSet();
    private boolean isAllObjectIdsMigrated;

    @Override
    public boolean hasErrors() {
      return false;
    }
}
