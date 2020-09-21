package com.roberttisma.tools.intermediate_song_importer.model.id;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class GenomicEntity {
  @NonNull private final String studyId;
  @NonNull private final String submitterId;
  @NonNull private final GenomicTypes genomicType;
}
