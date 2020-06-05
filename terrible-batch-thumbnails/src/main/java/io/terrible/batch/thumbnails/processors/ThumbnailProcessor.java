/* Licensed under Apache-2.0 */
package io.terrible.batch.thumbnails.processors;

import io.terrible.batch.data.domain.MediaFile;
import io.terrible.batch.thumbnails.exceptions.UnableToCalculateDurationException;
import io.terrible.batch.thumbnails.exceptions.UnableToReadFileException;
import io.terrible.batch.thumbnails.services.ThumbnailService;
import io.terrible.batch.thumbnails.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Slf4j
@Component
@RequiredArgsConstructor
public class ThumbnailProcessor implements ItemProcessor<MediaFile, MediaFile> {

  @Value("${batch.thumbnails.default}")
  private String baseDir;

  private static final int NUMBER_OF_THUMBNAILS = 12;

  private final ThumbnailService thumbnailService;

  @Override
  public MediaFile process(final MediaFile mediaFile) {

    mediaFile.setThumbnailPath(FileUtils.getThumbnailDirectory(baseDir, mediaFile));

    final Path input = Path.of(mediaFile.getPath());
    final Path output = Path.of(mediaFile.getThumbnailPath());

    try {
      mediaFile.setThumbnails(
          thumbnailService.createThumbnails(input, output, NUMBER_OF_THUMBNAILS));

      log.info("Thumbnails done for: {}", mediaFile.getName());
    } catch (UnableToCalculateDurationException | UnableToReadFileException exception) {
      mediaFile.setIgnored(true);
    }

    return mediaFile;
  }
}
