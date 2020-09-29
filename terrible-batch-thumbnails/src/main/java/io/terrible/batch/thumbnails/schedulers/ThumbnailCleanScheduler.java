/* Licensed under Apache-2.0 */
package io.terrible.batch.thumbnails.schedulers;

import io.terrible.batch.data.domain.MediaFile;
import io.terrible.batch.data.repository.MediaFileRepository;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class ThumbnailCleanScheduler {

  private final ExecutorService executor;

  private final MediaFileRepository mediaFileRepository;

  @Async
  @Scheduled(fixedDelay = 86400000)
  public void execute() {

    log.info("ThumbnailCleanScheduler started");

    mediaFileRepository.findAllByOrderByCreatedTimeDesc().stream()
        .filter(mediaFile -> !mediaFile.isIgnored())
        .forEach(consume());

    log.info("ThumbnailCleanScheduler finished");
  }

  private Consumer<MediaFile> consume() {
    return mediaFile ->
        executor.submit(
            () -> {
              mediaFile.getThumbnails().removeIf(path -> !new File(path).isFile());
              mediaFileRepository.save(mediaFile);
            });
  }
}
