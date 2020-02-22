package com.roberttisma.tools.intermediate_song_importer.service;

import static com.google.common.collect.Lists.partition;
import static com.roberttisma.tools.intermediate_song_importer.Factory.createDBUpdater;
import static com.roberttisma.tools.intermediate_song_importer.Factory.createMigrationService;
import static com.roberttisma.tools.intermediate_song_importer.util.FileIO.listFilesInDir;
import static java.util.concurrent.Executors.newFixedThreadPool;

import com.roberttisma.tools.intermediate_song_importer.model.ImporterSpec;
import com.roberttisma.tools.intermediate_song_importer.model.ProfileConfig;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

@Builder
@RequiredArgsConstructor
public class ProcessService implements Runnable {

  @NonNull private final ProfileConfig profileConfig;
  @NonNull private final Path inputDir;
  private final int numThreads;

  @Override
  @SneakyThrows
  public void run() {
    val executorService = newFixedThreadPool(numThreads);
    val files = listFilesInDir(inputDir, true);
    try (val dbUpdater = createDBUpdater(profileConfig.getTargetSong().getDb())) {
      val service =
          createMigrationService(
              profileConfig.getSourceSong(), profileConfig.getTargetSong(), dbUpdater);
      val importerSpecs = service.readImporterSpecs(files);

      // Initialize all the studyIds on the target song
      service.initTargetStudyIds(importerSpecs);

      // Concurrently run migrations
      partition(importerSpecs, numThreads)
          .forEach(p -> executorService.submit(createMigrationJob(service, p)));

      executorService.shutdown();
      executorService.awaitTermination(1L, TimeUnit.DAYS);
    }
  }

  private static Runnable createMigrationJob(MigrationService service, List<ImporterSpec> importerSpecs) {
    return () -> importerSpecs.forEach(service::migrate);
  }
}
