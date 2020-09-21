package com.roberttisma.tools.intermediate_song_importer.service;

import bio.overture.song.core.model.AnalysisTypeId;
import com.roberttisma.tools.intermediate_song_importer.exceptions.ImporterException;
import com.roberttisma.tools.intermediate_song_importer.model.report.AnalysisTypeErrorType;
import com.roberttisma.tools.intermediate_song_importer.model.report.AnalysisTypeValidationErrorReport;
import com.roberttisma.tools.intermediate_song_importer.model.report.Report;
import com.roberttisma.tools.intermediate_song_importer.util.Payloads;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.roberttisma.tools.intermediate_song_importer.model.report.AnalysisTypeErrorType.VERSION_DOES_NOT_EXIST;
import static com.roberttisma.tools.intermediate_song_importer.model.report.AnalysisTypeErrorType.VERSION_IS_OUTDATED;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toUnmodifiableSet;

@Slf4j
@RequiredArgsConstructor
public class AnalysisTypeValidationService {

  @NonNull private final TargetSongService targetSongService;

  public List<Report> validateLatestAnalysisType(@NonNull Collection<Path> jsonFiles){

    // Create a map of analysisTypeIds to list of files
    val currentAnalysisTypeIdMap = jsonFiles.stream()
        .collect(groupingBy(Payloads::parseAnalysisTypeId));


    // Get names of analysisTypes
    val names = currentAnalysisTypeIdMap.keySet().stream().map(AnalysisTypeId::getName).collect(toUnmodifiableSet());

    // Find the latest analysisType versions for those names
    val latestAnalysisTypeIdMap = targetSongService.getLatestAnalysisTypeIds(names).stream()
        .collect(toMap(AnalysisTypeId::getName, AnalysisTypeId::getVersion));

    // Create error report
    val errors = new ArrayList<AnalysisTypeValidationErrorReport>();
    for (val name: latestAnalysisTypeIdMap.keySet()){
      val latestVersion = latestAnalysisTypeIdMap.get(name);

      val currentErrors = currentAnalysisTypeIdMap.keySet()
          .stream()
          .filter(x -> x.getName().equals(name))
          .filter(x -> !isNull(x.getVersion()) && !x.getVersion().equals(latestVersion))
          .map(x -> AnalysisTypeValidationErrorReport.builder()
                .name(x.getName())
                .currentVersion(x.getVersion())
                .latestVersion(latestVersion)
                .payloadFilenames(currentAnalysisTypeIdMap.get(x))
                .errorType(resolveErrorType(x.getVersion(), latestVersion))
                .build())
          .collect(toUnmodifiableSet());


      if (!currentErrors.isEmpty()){
        log.error("[ANALYSIS_TYPE_VALIDATION_ERROR]: Several files with the analysisType name '{}' do not have the latest version '{}'",
            name, latestVersion );
      }
      errors.addAll(currentErrors);
    }

    return List.copyOf(errors);
  }

  private static AnalysisTypeErrorType resolveErrorType(int currentVersion, int latestVersion){
    if (currentVersion < latestVersion){
      return VERSION_IS_OUTDATED;
    } else if (currentVersion > latestVersion){
      return VERSION_DOES_NOT_EXIST;
    } else {
      throw ImporterException.buildImporterException("Should not be here");
    }
  }

}
