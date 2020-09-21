package com.roberttisma.tools.intermediate_song_importer.model.id;

import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

import java.util.HashSet;
import java.util.Set;

@Value
public class SubmitterIdAgg {

  @Getter private final Set<String> submitterDonorIds = new HashSet<>();
  @Getter private final Set<String> submitterSpecimenIds = new HashSet<>();
  @Getter private final Set<String> submitterSampleIds = new HashSet<>();

  public void addSubmitterDonorId(@NonNull String submitterId){
    submitterDonorIds.add(submitterId);
  }

  public void addSubmitterSpecimenId(@NonNull String submitterId){
    submitterSpecimenIds.add(submitterId);
  }

  public void addSubmitterSampleId(@NonNull String submitterId){
    submitterSampleIds.add(submitterId);
  }

}
