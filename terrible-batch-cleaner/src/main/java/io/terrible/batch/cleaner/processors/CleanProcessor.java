/* Licensed under Apache-2.0 */
package io.terrible.batch.cleaner.processors;

import io.terrible.batch.data.domain.MediaFile;
import io.terrible.batch.data.repository.MediaFileRepository;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;

@Slf4j
@RequiredArgsConstructor
public class CleanProcessor implements ItemProcessor<MediaFile, MediaFile> {

  private final MediaFileRepository mediaFileRepository;

  @Override
  public MediaFile process(@NonNull final MediaFile input) {

    if (!Files.exists(Paths.get(input.getPath()), LinkOption.NOFOLLOW_LINKS)) {

      log.info("Cannot find {} - Removing record", input.getName());

      FileUtils.deleteQuietly(new File(input.getThumbnailPath()));
      mediaFileRepository.delete(input);
    }

    return null;
  }
}
