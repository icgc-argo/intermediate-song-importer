package com.roberttisma.tools.intermediate_song_importer.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class SourceData {
  @NonNull private final String studyId;
  @NonNull private final String analysisId;
}
