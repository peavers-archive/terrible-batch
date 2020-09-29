/* Licensed under Apache-2.0 */
package io.terrible.batch.directory.schedulers;

import io.terrible.batch.data.domain.Directory;
import io.terrible.batch.data.repository.DirectoryRepository;
import io.terrible.batch.directory.processors.DirectoryProcessor;
import io.terrible.batch.directory.services.ScanService;
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
public class DirectoryScheduler {

  private final ExecutorService executor;

  private final DirectoryProcessor processor;

  private final DirectoryRepository directoryRepository;

  private final ScanService scanService;

  @Async
  @Scheduled(fixedDelay = 900000)
  public void schedule() {
    log.info("DirectoryScheduler started");
    directoryRepository.findAll().forEach(consume());
    log.info("DirectoryScheduler finished");
  }

  private Consumer<Directory> consume() {

    return directory ->
        executor.submit(
            () -> scanService.scanVideos(directory.getPath()).forEach(processor::process));
  }
}
