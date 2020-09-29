/* Licensed under Apache-2.0 */
package io.terrible.batch.cleaner.schedulers;

import io.terrible.batch.cleaner.processors.CleanProcessor;
import io.terrible.batch.data.domain.MediaFile;
import io.terrible.batch.data.repository.MediaFileRepository;
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
public class CleanScheduler {

  private final ExecutorService executor;

  private final MediaFileRepository mediaFileRepository;

  private final CleanProcessor processor;

  @Async
  @Scheduled(fixedDelay = 900000)
  public void schedule() {
    log.info("CleanScheduler started");
    mediaFileRepository.findAll().forEach(consume());
    log.info("CleanScheduler finished");
  }

  private Consumer<MediaFile> consume() {
    return mediaFile -> executor.submit(() -> processor.process(mediaFile));
  }
}
