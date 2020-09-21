package com.roberttisma.tools.intermediate_song_importer.model;

import static com.roberttisma.tools.intermediate_song_importer.util.Fields.checkRequiredField;
import static com.roberttisma.tools.intermediate_song_importer.util.Fields.mergeField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldNameConstants;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class IdConfig implements Mergable<IdConfig> {

  private String donorUrlTemplate;
  private String specimenUrlTemplate;
  private String sampleUrlTemplate;

  @Override
  public void merge(IdConfig idConfig) {
    mergeField(IdConfig::getDonorUrlTemplate, IdConfig::setDonorUrlTemplate, this, idConfig);
    mergeField(IdConfig::getSpecimenUrlTemplate, IdConfig::setSpecimenUrlTemplate, this, idConfig);
    mergeField(IdConfig::getSampleUrlTemplate, IdConfig::setSampleUrlTemplate, this, idConfig);
  }

  public static void checkIdConfig(@NonNull IdConfig idConfig) {
    checkRequiredField(Fields.donorUrlTemplate, idConfig.getDonorUrlTemplate());
    checkRequiredField(Fields.specimenUrlTemplate, idConfig.getSpecimenUrlTemplate());
    checkRequiredField(Fields.sampleUrlTemplate, idConfig.getSampleUrlTemplate());
  }
}
