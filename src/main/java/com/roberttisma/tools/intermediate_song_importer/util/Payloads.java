package com.roberttisma.tools.intermediate_song_importer.util;

import static com.roberttisma.tools.intermediate_song_importer.util.JsonUtils.checkRequiredField;
import static com.roberttisma.tools.intermediate_song_importer.util.JsonUtils.readTree;

import java.nio.file.Path;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.val;

public class Payloads {

  private static final String STUDY_ID = "studyId";
  private static final String DONOR = "donor";
  private static final String SUBMITTER_DONOR_ID = "submitterDonorId";
  private static final String SUBMITTER_SPECIMEN_ID = "submitterSpecimenId";
  private static final String SPECIMEN = "specimen";
  private static final String SUBMITTER_SAMPLE_ID = "submitterSampleId";
  private static final String SAMPLES = "samples";

  public static String parseStudyId(Path jsonPath) {
    val root = readTree(jsonPath);
    return parseStudyId(root);
  }

  public static String parseStudyId(JsonNode root) {
    checkRequiredField(root, STUDY_ID);
    return root.path(STUDY_ID).asText();
  }

  public static String parseSubmitterDonorId(JsonNode sample) {
    checkRequiredField(sample, DONOR);
    val donor = sample.path(DONOR);
    checkRequiredField(donor, SUBMITTER_DONOR_ID);
    return donor.path(SUBMITTER_DONOR_ID).asText();
  }

  public static String parseSubmitterSpecimenId(JsonNode sample) {
    checkRequiredField(sample, SPECIMEN);
    val specimen = sample.path(SPECIMEN);
    checkRequiredField(specimen, SUBMITTER_SPECIMEN_ID);
    return specimen.path(SUBMITTER_SPECIMEN_ID).asText();
  }

  public static String parseSubmitterSampleId(JsonNode sample) {
    checkRequiredField(sample, SUBMITTER_SAMPLE_ID);
    return sample.path(SUBMITTER_SAMPLE_ID).asText();
  }

  public static JsonNode parseSamples(JsonNode root) {
    checkRequiredField(root, SAMPLES);
    return root.path(SAMPLES);
  }



}
