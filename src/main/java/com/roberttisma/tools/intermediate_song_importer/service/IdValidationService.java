package com.roberttisma.tools.intermediate_song_importer.service;

import static bio.overture.song.core.utils.Streams.stream;
import static com.roberttisma.tools.intermediate_song_importer.exceptions.ImporterException.buildImporterException;
import static com.roberttisma.tools.intermediate_song_importer.util.JsonUtils.readTree;
import static com.roberttisma.tools.intermediate_song_importer.util.Payloads.parseSamples;
import static com.roberttisma.tools.intermediate_song_importer.util.Payloads.parseStudyId;
import static com.roberttisma.tools.intermediate_song_importer.util.Payloads.parseSubmitterDonorId;
import static com.roberttisma.tools.intermediate_song_importer.util.Payloads.parseSubmitterSampleId;
import static com.roberttisma.tools.intermediate_song_importer.util.Payloads.parseSubmitterSpecimenId;

import com.fasterxml.jackson.databind.JsonNode;
import com.roberttisma.tools.intermediate_song_importer.model.id.GenomicEntity;
import com.roberttisma.tools.intermediate_song_importer.model.id.GenomicEntityMap;
import com.roberttisma.tools.intermediate_song_importer.service.id.IdService;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
@RequiredArgsConstructor
public class IdValidationService {

  @NonNull private final IdService idService;

  public Stream<GenomicEntity> validate(@NonNull Collection<Path> jsonFiles) {
    val genomicEntityMap = buildGenomicEntityMap(jsonFiles);
    return genomicEntityMap.streamGenomicEntities().filter(this::isNonExistent);
  }

  private boolean isNonExistent(GenomicEntity genomicEntity) {
    val genomicType = genomicEntity.getGenomicType();
    switch (genomicType) {
      case DONOR:
        return idService
            .getDonorId(genomicEntity.getStudyId(), genomicEntity.getSubmitterId())
            .isEmpty();
      case SPECIMEN:
        return idService
            .getSpecimenId(genomicEntity.getStudyId(), genomicEntity.getSubmitterId())
            .isEmpty();
      case SAMPLE:
        return idService
            .getSampleId(genomicEntity.getStudyId(), genomicEntity.getSubmitterId())
            .isEmpty();
      default:
        throw buildImporterException("Cannot process genomicType '%s'", genomicType);
    }
  }

  private GenomicEntityMap buildGenomicEntityMap(@NonNull Collection<Path> jsonFiles) {
    val map = new GenomicEntityMap();
    jsonFiles.forEach(
        jsonFile -> {
          val root = readTree(jsonFile);
          val studyId = parseStudyId(root);
          val samples = parseSamples(root);
          stream(samples).forEach(s -> parseSample(map, studyId, s));
        });
    return map;
  }

  private static void parseSample(GenomicEntityMap map, String studyId, JsonNode sample) {
    val submitterDonorId = parseSubmitterDonorId(sample);
    val submitterSpecimenId = parseSubmitterSpecimenId(sample);
    val submitterSampleId = parseSubmitterSampleId(sample);
    map.addDonorEntity(studyId, submitterDonorId);
    map.addSpecimenEntity(studyId, submitterSpecimenId);
    map.addSampleEntity(studyId, submitterSampleId);
  }
}
