/* Licensed under Apache-2.0 */
package io.terrible.batch.thumbnails.processors;

import io.terrible.batch.data.domain.MediaFile;
import io.terrible.batch.thumbnails.services.ThumbnailService;
import io.terrible.batch.thumbnails.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import java.nio.file.Path;

@Slf4j
@RequiredArgsConstructor
public class ThumbnailProcessor implements ItemProcessor<MediaFile, MediaFile> {

  private static final int NUMBER_OF_THUMBNAILS = 12;

  private final ThumbnailService thumbnailService;

  @Override
  public MediaFile process(final MediaFile mediaFile) {

    mediaFile.setThumbnailPath(FileUtils.getThumbnailDirectory(mediaFile));

    final Path input = Path.of(mediaFile.getPath());
    final Path output = Path.of(mediaFile.getThumbnailPath());

    mediaFile.setThumbnails(thumbnailService.createThumbnails(input, output, NUMBER_OF_THUMBNAILS));

    log.info("Thumbnails done for: {}", mediaFile.getName());

    return mediaFile;
  }
}
