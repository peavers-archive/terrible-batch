/* Licensed under Apache-2.0 */
package io.terrible.batch.thumbnails.services;

import java.nio.file.Path;
import java.util.ArrayDeque;

public interface ThumbnailService {

  ArrayDeque<String> createThumbnails(Path input, Path output, int count);
}
