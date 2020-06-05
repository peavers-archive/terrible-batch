package io.terrible.batch.thumbnails.exceptions;

import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

@Slf4j
public class UnableToCalculateDurationException extends Throwable {

  public UnableToCalculateDurationException(final Path fileName) {
    super();

    log.error("Unable to calculate duration for: {}", fileName);
  }
}
