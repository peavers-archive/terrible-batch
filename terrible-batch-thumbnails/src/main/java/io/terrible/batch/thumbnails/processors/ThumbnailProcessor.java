/* Licensed under Apache-2.0 */
package io.terrible.batch.thumbnails.processors;

import io.terrible.batch.data.domain.MediaFile;
import io.terrible.batch.thumbnails.exceptions.UnableToCalculateDurationException;
import io.terrible.batch.thumbnails.exceptions.UnableToReadFileException;
import io.terrible.batch.thumbnails.services.ThumbnailService;
import io.terrible.batch.thumbnails.utils.FileUtils;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ThumbnailProcessor {

  private static final int NUMBER_OF_THUMBNAILS = 12;

  private final ThumbnailService thumbnailService;

  @Value("${batch.thumbnails.default}")
  private String baseDir;

  public MediaFile process(final MediaFile mediaFile) {
    ArrayDeque<String> thumbnails = new ArrayDeque<>(NUMBER_OF_THUMBNAILS);

    mediaFile.setThumbnailPath(FileUtils.getThumbnailDirectory(baseDir, mediaFile));

    final Path input = Paths.get(mediaFile.getPath());
    final Path output = Paths.get(mediaFile.getThumbnailPath());

    try {
      thumbnails = thumbnailService.createThumbnails(input, output, NUMBER_OF_THUMBNAILS);
    } catch (Exception | UnableToCalculateDurationException | UnableToReadFileException exception) {
      mediaFile.setIgnored(true);
      log.info(
          "Marking {} as ignored due to error {}", mediaFile.getName(), exception.getMessage());
      log.error("Error {} {}", exception, exception.getMessage());
    }

    mediaFile.setThumbnails(thumbnails);

    return mediaFile;
  }
}
