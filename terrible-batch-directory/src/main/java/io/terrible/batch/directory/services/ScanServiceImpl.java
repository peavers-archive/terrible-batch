/* Licensed under Apache-2.0 */
package io.terrible.batch.directory.services;

import com.google.common.net.MediaType;
import io.terrible.batch.data.domain.MediaFile;
import io.terrible.batch.directory.converters.MediaFileConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScanServiceImpl implements ScanService {

  @Override
  public ArrayDeque<MediaFile> scanVideos(final String input) {

    final ArrayDeque<MediaFile> results = new ArrayDeque<>();

    if (StringUtils.isEmpty(input)) {
      return results;
    }

    try {
      Files.walk(Path.of(input)).filter(Files::isReadable).forEach(path -> process(path, results));
    } catch (IOException e) {
      log.warn("Issue processing stream {}", e.getMessage());
    }

    return results;
  }

  private void process(final Path path, final ArrayDeque<MediaFile> results) {

    try {
      final String mimeType = Files.probeContentType(path);

      //noinspection UnstableApiUsage
      if (StringUtils.isNoneEmpty(mimeType)
          && !path.toFile().getAbsolutePath().contains("sample")
          && MediaType.parse(mimeType).is(MediaType.ANY_VIDEO_TYPE)) {

        results.add(MediaFileConverter.convert(path.toFile()));
      }

    } catch (IOException e) {
      log.error("Unable to prob file {}", path);
    }
  }
}
