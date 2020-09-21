package com.roberttisma.tools.intermediate_song_importer.service;

import static com.google.common.collect.Lists.newArrayList;
import static com.roberttisma.tools.intermediate_song_importer.exceptions.ImporterException.checkImporter;
import static com.roberttisma.tools.intermediate_song_importer.util.Joiners.COMMA_SPACE;
import static com.roberttisma.tools.intermediate_song_importer.util.JsonUtils.checkRequiredField;
import static com.roberttisma.tools.intermediate_song_importer.util.JsonUtils.mapper;
import static com.roberttisma.tools.intermediate_song_importer.util.JsonUtils.readTree;
import static java.lang.String.format;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableSet;

import bio.overture.song.core.model.FileDTO;
import bio.overture.song.sdk.SongApi;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Sets;
import com.roberttisma.tools.intermediate_song_importer.model.SongConfig;
import com.roberttisma.tools.intermediate_song_importer.model.SourceData;
import com.roberttisma.tools.intermediate_song_importer.util.RestClient;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

@Builder
@RequiredArgsConstructor
public class SourceSongService {

  private static final String LEGACY_ANALYSIS_ID = "legacyAnalysisId";
  private static final String FILES = "files";

  @NonNull private RestClient restClient;
  @NonNull private SongApi api;
  @NonNull private SongConfig config;

  public List<FileDTO> getSourceAnalysisFiles(@NonNull Path payloadFile) {
    return processSourceData(payloadFile).stream()
        .map(this::getSourceAnalysisFiles)
        .flatMap(Collection::stream)
        .collect(toUnmodifiableList());
  }

  private List<FileDTO> getSourceAnalysisFiles(SourceData d) {
    return api.getAnalysisFiles(d.getStudyId(), d.getAnalysisId());
  }

  private List<SourceData> processSourceData(Path file) {
    val legacyAnalysisIds = parseLegacyAnalysisIds(file);
    val legacyStudyIds =
        legacyAnalysisIds.stream()
            .map(this::getStudyForAnalysisId)
            .distinct()
            .collect(toUnmodifiableList());

    // Check that all legacyAnalysisIds in a payload belong to the same ICGC studyId
    checkImporter(
        legacyStudyIds.size() == 1,
        "The legacyAnalysisIds for file '%s' must belong to only 1 legacyStudyId, "
            + "but instead belong to legacyStudyIds: [%s]",
        file.toString(),
        COMMA_SPACE.join(legacyAnalysisIds));
    val legacyStudyId = legacyStudyIds.get(0);

    return legacyAnalysisIds.stream()
        .map(a -> SourceData.builder().analysisId(a).studyId(legacyStudyId).build())
        .collect(toUnmodifiableList());
  }

  @SneakyThrows
  private String getStudyForAnalysisId(@NonNull String analysisId) {
    val response = restClient.get(getLegacyEntityUrl(analysisId));
    val j = mapper().readTree(response.getBody());
    val contentNode = parseContent(j);
    checkImporter(
        !contentNode.isEmpty(),
        "The analysisId '%s' does not exist at '%s'",
        analysisId,
        config.getServerUrl());
    return contentNode.path(0).path("projectCode").asText();
  }

  private String getLegacyEntityUrl(String analysisId) {
    return format("%s/entities?gnosId=%s", config.getServerUrl(), analysisId);
  }

  private static Set<String> parseLegacyAnalysisIds(Path file) {
    val fileNamesMissingLegacyAnalysisId = Sets.<String>newHashSet();
    val legacyAnalysisIds = Sets.<String>newHashSet();
    parseFiles(file)
        .forEach(
            f -> {
              val result = parseLegacyAnalysisId(f);
              if (result.isEmpty()) {
                fileNamesMissingLegacyAnalysisId.add(f.getFileName());
              } else {
                legacyAnalysisIds.add(result.get());
              }
            });
    checkImporter(
        fileNamesMissingLegacyAnalysisId.isEmpty(),
        "The following filenames for payload file '%s' are missing the info field '%s'",
        file.toString(),
        COMMA_SPACE.join(fileNamesMissingLegacyAnalysisId));
    return Set.copyOf(legacyAnalysisIds);
  }

  private static Optional<String> parseLegacyAnalysisId(FileDTO f) {
    val info = f.getInfo();
    if (info.has(LEGACY_ANALYSIS_ID)) {
      return Optional.ofNullable(info.path(LEGACY_ANALYSIS_ID).textValue());
    } else {
      return Optional.empty();
    }
  }

  private static Set<FileDTO> parseFiles(Path file) {
    checkFileNameFormat(file);
    val payload = readTree(file);
    checkImporter(payload.has("files"), "The file '%s' does not contain the field 'files'", file);
    return newArrayList(payload.path("files").iterator()).stream()
        .map(x -> mapper().convertValue(x, FileDTO.class))
        .collect(toUnmodifiableSet());
  }

  private static void checkFileNameFormat(Path targetPayloadFile) {
    checkImporter(
        targetPayloadFile.toString().endsWith(".json"),
        "The file '%s' is not a json file",
        targetPayloadFile.toString());
  }

  private static JsonNode parseContent(JsonNode root) {
    checkRequiredField(root, "content");
    return root.path("content");
  }
}
