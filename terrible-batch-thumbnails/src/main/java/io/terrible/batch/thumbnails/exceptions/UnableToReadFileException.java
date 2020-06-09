/* Licensed under Apache-2.0 */
package io.terrible.batch.thumbnails.exceptions;

import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UnableToReadFileException extends Throwable {

  public UnableToReadFileException(final Path fileName) {
    super();

    log.error("Unable to read file: {}", fileName);
  }
}
