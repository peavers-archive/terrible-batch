/* Licensed under Apache-2.0 */
package io.terrible.batch.cleaner.schedulers;

import io.terrible.batch.cleaner.processors.CleanProcessor;
import io.terrible.batch.data.domain.MediaFile;
import io.terrible.batch.data.domain.MediaList;
import io.terrible.batch.data.repository.MediaFileRepository;
import io.terrible.batch.data.repository.MediaListRepository;

import java.io.File;
import java.util.Iterator;
import java.util.Optional;
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
public class CleanSchedulerLists {

  private final MediaFileRepository mediaFileRepository;

  private final MediaListRepository mediaListRepository;

  @Async
  @Scheduled(fixedDelay = 900000)
  public void schedule() {
    log.info("CleanSchedulerLists started");

    mediaListRepository.findAll().forEach(mediaList -> {
      Iterator<MediaFile> iterator = mediaList.getMediaFiles().iterator();

      while(iterator.hasNext()) {
        MediaFile mediaFile = iterator.next();

        Optional<MediaFile> optionalMediaFile = mediaFileRepository.findById(mediaFile.getId());

        if(!optionalMediaFile.isPresent()) {
          iterator.remove();
        }
      }

      mediaListRepository.save(mediaList);
    });

    log.info("CleanSchedulerLists finished");
  }


}
