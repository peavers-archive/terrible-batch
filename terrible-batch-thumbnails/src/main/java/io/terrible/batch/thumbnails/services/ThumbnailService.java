/* Licensed under Apache-2.0 */
package io.terrible.batch.thumbnails.services;

import io.terrible.batch.thumbnails.exceptions.UnableToCalculateDurationException;
import io.terrible.batch.thumbnails.exceptions.UnableToReadFileException;

import java.nio.file.Path;
import java.util.ArrayDeque;

public interface ThumbnailService {

  ArrayDeque<String> createThumbnails(Path input, Path output, int count)
          throws UnableToCalculateDurationException, UnableToReadFileException;
}
