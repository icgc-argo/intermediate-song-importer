package com.roberttisma.tools.intermediate_song_importer.service.id;

import static bio.overture.song.core.utils.CollectionUtils.listDifference;
import static bio.overture.song.core.utils.Separators.COMMA;
import static com.google.common.base.Preconditions.checkArgument;
import static lombok.AccessLevel.PRIVATE;

import com.roberttisma.tools.intermediate_song_importer.model.IdConfig;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.web.util.UriTemplate;

/**
 * Dynamically expands URIs. Using the pre-configured URI templates, the URIs are resolved against
 * the required inputs
 */
@Builder
@RequiredArgsConstructor(access = PRIVATE)
public class UriResolver {

  /** Constants */
  private static final String SUBMITTER_ID = "submitterId";

  private static final String STUDY_ID = "studyId";

  /** Dependencies */
  @NonNull private final UriTemplate donorUriTemplate;

  @NonNull private final UriTemplate specimenUriTemplate;
  @NonNull private final UriTemplate sampleUriTemplate;

  public String expandDonorUri(@NonNull String studyId, @NonNull String submitterId) {
    return donorUriTemplate.expand(Map.of(STUDY_ID, studyId, SUBMITTER_ID, submitterId)).toString();
  }

  public String expandSpecimenUri(@NonNull String studyId, @NonNull String submitterId) {
    return specimenUriTemplate
        .expand(Map.of(STUDY_ID, studyId, SUBMITTER_ID, submitterId))
        .toString();
  }

  public String expandSampleUri(@NonNull String studyId, @NonNull String submitterId) {
    return sampleUriTemplate
        .expand(Map.of(STUDY_ID, studyId, SUBMITTER_ID, submitterId))
        .toString();
  }

  /** Processes the defined URI templates and instantiates the UriResolver */
  public static UriResolver createUriResolver(@NonNull IdConfig idConfig) {
    return UriResolver.builder()
        .donorUriTemplate(processTemplate(idConfig.getDonorUrlTemplate(), STUDY_ID, SUBMITTER_ID))
        .specimenUriTemplate(
            processTemplate(idConfig.getSpecimenUrlTemplate(), STUDY_ID, SUBMITTER_ID))
        .sampleUriTemplate(processTemplate(idConfig.getSampleUrlTemplate(), STUDY_ID, SUBMITTER_ID))
        .build();
  }

  /**
   * Ensures the uri template string contains the required template variables, and returns a
   * UriTemplate object
   */
  private static UriTemplate processTemplate(
      String templateString, String... requiredTemplateVariables) {
    val uriTemplate = new UriTemplate(templateString);
    val actualVariables = List.copyOf(uriTemplate.getVariableNames());
    val requiredVariables = List.of(requiredTemplateVariables);
    val missingList = listDifference(requiredVariables, actualVariables);
    val unknownList = listDifference(actualVariables, requiredVariables);
    checkArgument(
        missingList.isEmpty() && unknownList.isEmpty(),
        "Error processing URI template string: '%s'. %s%s",
        templateString,
        missingList.isEmpty()
            ? ""
            : "Missing template variables: [" + COMMA.join(missingList) + "]. ",
        unknownList.isEmpty()
            ? ""
            : "Unknown template variables: [" + COMMA.join(unknownList) + "]. ");
    return uriTemplate;
  }
}
