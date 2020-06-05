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
import java.util.ArrayDeque;

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
    ArrayDeque<String> thumbnails = new ArrayDeque<>(NUMBER_OF_THUMBNAILS);

    mediaFile.setThumbnailPath(FileUtils.getThumbnailDirectory(baseDir, mediaFile));

    final Path input = Path.of(mediaFile.getPath());
    final Path output = Path.of(mediaFile.getThumbnailPath());

    try {
      thumbnails = thumbnailService.createThumbnails(input, output, NUMBER_OF_THUMBNAILS);
    } catch (UnableToCalculateDurationException | UnableToReadFileException | Exception exception) {
      mediaFile.setIgnored(true);
    }

    mediaFile.setThumbnails(thumbnails);

    return mediaFile;
  }
}
