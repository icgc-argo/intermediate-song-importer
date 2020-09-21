package com.roberttisma.tools.intermediate_song_importer.model.id;

import static com.google.common.collect.Streams.concat;
import static com.roberttisma.tools.intermediate_song_importer.model.id.GenomicTypes.DONOR;
import static com.roberttisma.tools.intermediate_song_importer.model.id.GenomicTypes.SAMPLE;
import static com.roberttisma.tools.intermediate_song_importer.model.id.GenomicTypes.SPECIMEN;
import static java.util.function.Function.identity;
import static java.util.stream.Stream.concat;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.val;

public class GenomicEntityMap {

  private final Map<String, SubmitterIdAgg> map = new HashMap<>();

  public void addDonorEntity(@NonNull String studyId, @NonNull String submitterDonorId) {
    getSubmitterIdAgg(studyId).addSubmitterDonorId(submitterDonorId);
  }

  public void addSpecimenEntity(@NonNull String studyId, @NonNull String submitterSpecimenId) {
    getSubmitterIdAgg(studyId).addSubmitterSpecimenId(submitterSpecimenId);
  }

  public void addSampleEntity(@NonNull String studyId, @NonNull String submitterSampleId) {
    getSubmitterIdAgg(studyId).addSubmitterSampleId(submitterSampleId);
  }

  public Stream<GenomicEntity> streamGenomicEntities() {
    return map.entrySet().parallelStream().map(this::streamEntry).flatMap(identity());
  }

  private SubmitterIdAgg getSubmitterIdAgg(String studyId) {
    if (!map.containsKey(studyId)) {
      map.put(studyId, new SubmitterIdAgg());
    }
    return map.get(studyId);
  }

  private Stream<GenomicEntity> streamEntry(Entry<String, SubmitterIdAgg> entry) {
    return concat(concat(streamDonors(entry), streamSpecimens(entry)), streamSamples(entry));
  }

  private Stream<GenomicEntity> streamDonors(Entry<String, SubmitterIdAgg> entry) {
    return streamEntity(DONOR, SubmitterIdAgg::getSubmitterDonorIds, entry);
  }

  private Stream<GenomicEntity> streamSpecimens(Entry<String, SubmitterIdAgg> entry) {
    return streamEntity(SPECIMEN, SubmitterIdAgg::getSubmitterSpecimenIds, entry);
  }

  private Stream<GenomicEntity> streamSamples(Entry<String, SubmitterIdAgg> entry) {
    return streamEntity(SAMPLE, SubmitterIdAgg::getSubmitterSampleIds, entry);
  }

  private Stream<GenomicEntity> streamEntity(
      GenomicTypes genomicType,
      Function<SubmitterIdAgg, Set<String>> getter,
      Entry<String, SubmitterIdAgg> entry) {
    val submitterIdAgg = entry.getValue();
    val studyId = entry.getKey();
    return getter
        .apply(submitterIdAgg)
        .parallelStream()
        .map(
            x ->
                GenomicEntity.builder()
                    .submitterId(x)
                    .genomicType(genomicType)
                    .studyId(studyId)
                    .build());
  }
}
