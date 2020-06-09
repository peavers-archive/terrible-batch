/* Licensed under Apache-2.0 */
package io.terrible.batch.thumbnails.exceptions;

import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UnableToCalculateDurationException extends Throwable {

  public UnableToCalculateDurationException(final Path fileName) {
    super();

    log.error("Unable to calculate duration for: {}", fileName);
  }
}
