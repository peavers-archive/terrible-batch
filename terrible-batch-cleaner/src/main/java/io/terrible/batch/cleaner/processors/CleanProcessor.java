/* Licensed under Apache-2.0 */
package io.terrible.batch.cleaner.processors;

import io.terrible.batch.data.domain.MediaFile;
import io.terrible.batch.data.repository.MediaFileRepository;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CleanProcessor {

  private final MediaFileRepository mediaFileRepository;

  public MediaFile process(@NonNull final MediaFile mediaFile) {

    final Path path = Paths.get(mediaFile.getPath());

    if (mediaFile.isDelete()) {
      log.info("{} - Flagged for deletion", mediaFile.getName());

      FileUtils.deleteQuietly(new File(mediaFile.getThumbnailPath()));
      FileUtils.deleteQuietly(new File(mediaFile.getPath()));
      mediaFileRepository.delete(mediaFile);
    } else if (Files.notExists(path)) {
      log.info("Cannot find {} - Removing record", mediaFile.getName());

      if (StringUtils.isNotBlank(mediaFile.getThumbnailPath())) {
        FileUtils.deleteQuietly(new File(mediaFile.getThumbnailPath()));
      }

      mediaFileRepository.delete(mediaFile);
    }

    return null;
  }
}
