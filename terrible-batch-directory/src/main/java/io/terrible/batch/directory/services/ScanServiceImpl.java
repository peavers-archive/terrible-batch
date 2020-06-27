/* Licensed under Apache-2.0 */
package io.terrible.batch.directory.services;

import com.google.common.net.MediaType;
import io.terrible.batch.data.domain.MediaFile;
import io.terrible.batch.directory.converters.MediaFileConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScanServiceImpl implements ScanService {

  @Override
  public ArrayList<MediaFile> scanVideos(final String input) {

    try {
      return Files.walk(Paths.get(input))
          .filter(Files::isReadable)
          .filter(this::isAcceptedSize)
          .filter(this::isNotSample)
          .filter(this::isNotDirectory)
          .filter(this::isMediaFile)
          .map(this::convert)
          .collect(Collectors.toCollection(ArrayList::new));
    } catch (final Exception e) {
      return new ArrayList<>();
    }
  }

  private boolean isMediaFile(final Path path) {
    final String contentType = probeContentType(path);

    //noinspection UnstableApiUsage
    return StringUtils.isNotEmpty(contentType)
        && MediaType.parse(contentType).is(MediaType.ANY_VIDEO_TYPE);
  }

  private boolean isNotDirectory(final Path path) {
    return !Files.isDirectory(path);
  }

  private boolean isNotSample(final Path path) {
    return !Pattern.compile(Pattern.quote("sample"), Pattern.CASE_INSENSITIVE)
        .matcher(path.toString())
        .find();
  }

  private boolean isAcceptedSize(final Path path) {
    try {
      return Files.size(path) > 104857600; // 100mb
    } catch (final IOException e) {
      log.error("Unable to calculate file size {} {}", path, e.getMessage());
      return false;
    }
  }

  private String probeContentType(final Path path) {
    try {
      return Files.probeContentType(path);
    } catch (final IOException e) {
      log.error("Unable to probe {} {}", path, e.getMessage());
      return null;
    }
  }

  private MediaFile convert(final Path path) {
    return MediaFileConverter.convert(path.toFile());
  }
}
