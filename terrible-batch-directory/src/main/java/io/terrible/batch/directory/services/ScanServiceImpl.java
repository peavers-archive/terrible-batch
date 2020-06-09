/* Licensed under Apache-2.0 */
package io.terrible.batch.directory.services;

import com.google.common.net.MediaType;
import io.terrible.batch.data.domain.Directory;
import io.terrible.batch.data.domain.MediaFile;
import io.terrible.batch.data.repository.DirectoryRepository;
import io.terrible.batch.directory.converters.MediaFileConverter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScanServiceImpl implements ScanService {

  private final DirectoryRepository directoryRepository;

  @Override
  public ArrayDeque<MediaFile> scanVideos(final String input) {

    final Directory directory = directoryRepository.findAll().get(0);
    final ArrayDeque<MediaFile> results = new ArrayDeque<>();

    if (StringUtils.isEmpty(input)) {
      return results;
    }

    try {
      Files.walk(Paths.get(input))
          .filter(Files::isReadable)
          .forEach(path -> process(path, directory, results));
    } catch (final IOException e) {
      log.warn("Issue processing stream {} {}", e.getCause(), e);
    }

    return results;
  }

  private void process(
      final Path path, final Directory directory, final ArrayDeque<MediaFile> results) {

    try {
      final String mimeType = Files.probeContentType(path);

      //noinspection UnstableApiUsage
      if (StringUtils.isNoneEmpty(mimeType)
          && !path.toFile().getAbsolutePath().contains("sample")
          && MediaType.parse(mimeType).is(MediaType.ANY_VIDEO_TYPE)) {

        results.add(MediaFileConverter.convert(path.toFile(), directory));
      }

    } catch (final IOException e) {
      log.error("Unable to prob file {}", path);
    }
  }
}
