/* Licensed under Apache-2.0 */
package io.terrible.batch.thumbnails.services;

import io.terrible.batch.thumbnails.exceptions.UnableToCalculateDurationException;
import io.terrible.batch.thumbnails.exceptions.UnableToReadFileException;
import io.terrible.batch.thumbnails.utils.CommandUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;

import static org.apache.commons.math3.util.FastMath.round;

@Slf4j
@Service
@RequiredArgsConstructor
public class ThumbnailServiceImpl implements ThumbnailService {

  private final ProcessService processService;

  /**
   * Given a video file, divide its time length into the number of thumbnails to create, using
   * FFMPEG jump to those time stamps to grab the closest frame we find.
   */
  @Override
  public ArrayDeque<String> createThumbnails(final Path input, final Path output, final int count)
      throws UnableToCalculateDurationException, UnableToReadFileException {

    if (!Files.isReadable(input)) {
      throw new UnableToReadFileException(input.getFileName());
    }

    double duration = calculateDuration(input) / 60;

    final File outputDirectory = createOutputDirectory(output);
    final ArrayDeque<String> thumbnails = new ArrayDeque<>(count);

    for (int i = 1; i <= count; i++) {
      final Path thumbnailLocation = Path.of(String.format("%s/00%d.jpg", outputDirectory, i));

      final double timestamp = (i - 0.5) * (duration / count) * 60;

      try {
        processService.execute(
            CommandUtils.createThumbnail(
                String.valueOf(round(timestamp)),
                input.toFile().getAbsolutePath(),
                thumbnailLocation.toString()));

        thumbnails.add(thumbnailLocation.toString());
      } catch (final IOException | InterruptedException e) {
        log.error("Unable to create thumbnails {}", e.getMessage());
      }
    }

    return thumbnails;
  }

  /**
   * Use FFMPEG to calculate the total duration of the video. This is used to work out where to
   * create the thumbnails.
   */
  private double calculateDuration(final Path input) throws UnableToCalculateDurationException {

    try {
      final String output =
          processService.execute(CommandUtils.calculateDuration(input.toFile().getAbsolutePath()));

      return StringUtils.isNotBlank(output) ? Double.parseDouble(output) : -1;

    } catch (final IOException | InterruptedException e) {
      throw new UnableToCalculateDurationException(input.getFileName());
    }
  }

  /**
   * Attempt to create the output directory. If this fails, we break all rules and hard abort the
   * task as we don't care anymore. Don't really do this in the real world.
   */
  private File createOutputDirectory(final Path output) {

    final File outputFile = output.toFile();

    FileUtils.deleteQuietly(outputFile); // Empty the directory if its there.

    if (!outputFile.mkdirs()) {
      log.warn("Unable to create output directory, aborting {}", output);
    }

    return outputFile;
  }
}
