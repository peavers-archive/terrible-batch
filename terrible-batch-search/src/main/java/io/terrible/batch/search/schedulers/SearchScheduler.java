/* Licensed under Apache-2.0 */
package io.terrible.batch.search.schedulers;

import io.terrible.batch.data.domain.MediaFile;
import io.terrible.batch.data.repository.MediaFileRepository;
import io.terrible.batch.search.processors.SearchProcessor;
import java.util.Objects;
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
public class SearchScheduler {

  private final ExecutorService executor;

  private final MediaFileRepository mediaFileRepository;

  private final SearchProcessor processor;

  @Async
  @Scheduled(fixedDelay = 900000)
  public void execute() {
    log.info("SearchScheduler started");
    mediaFileRepository.findAll().stream()
        .filter(mediaFile -> !mediaFile.isIndexed())
        .forEach(consume());
    log.info("SearchScheduler finished");
  }

  private Consumer<MediaFile> consume() {
    return mediaFile ->
        executor.submit(
            () -> mediaFileRepository.save(Objects.requireNonNull(processor.process(mediaFile))));
  }
}
