package io.terrible.batch.thumbnails.exceptions;

import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

@Slf4j
public class UnableToReadFileException extends Throwable {

  public UnableToReadFileException(final Path fileName) {
    super();

    log.error("Unable to read file: {}", fileName);
  }
}
