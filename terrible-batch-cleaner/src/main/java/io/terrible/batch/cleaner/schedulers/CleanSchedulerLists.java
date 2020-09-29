/* Licensed under Apache-2.0 */
package io.terrible.batch.cleaner.schedulers;

import io.terrible.batch.data.domain.MediaFile;
import io.terrible.batch.data.domain.MediaList;
import io.terrible.batch.data.repository.MediaFileRepository;
import io.terrible.batch.data.repository.MediaListRepository;
import java.util.Iterator;
import java.util.Optional;
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

    mediaListRepository.findAll().forEach(this::updateThumbnails);

    log.info("CleanSchedulerLists finished");
  }

  private void updateThumbnails(final MediaList mediaList) {

    final Iterator<MediaFile> iterator = mediaList.getMediaFiles().iterator();

    while (iterator.hasNext()) {
      final MediaFile mediaFile = iterator.next();
      final Optional<MediaFile> optionalMediaFile = mediaFileRepository.findById(mediaFile.getId());

      if (optionalMediaFile.isPresent()) {
        mediaFile.setThumbnails(optionalMediaFile.get().getThumbnails());
      } else {
        iterator.remove();
      }
    }

    mediaListRepository.save(mediaList);
  }
}
