/* Licensed under Apache-2.0 */
package io.terrible.batch.services;

import com.google.common.net.MediaType;
import io.terrible.batch.converters.MediaFileConverter;
import io.terrible.batch.domain.MediaFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayDeque;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScanServiceImpl implements ScanService {

  @Override
  public ArrayDeque<MediaFile> scanVideos(final String input) throws IOException {

    if (input == null) {
      return new ArrayDeque<>(0);
    }

    final Collection<File> files =
        FileUtils.listFiles(new File(input), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);

    final ArrayDeque<MediaFile> results = new ArrayDeque<>(files.size());

    for (final File file : files) {
      final String mimeType = Files.probeContentType(file.toPath());

      //noinspection UnstableApiUsage
      if (StringUtils.isNoneEmpty(mimeType)
          && !file.getAbsolutePath().contains("sample")
          && MediaType.parse(mimeType).is(MediaType.ANY_VIDEO_TYPE)) {

        results.add(MediaFileConverter.convert(file));
      }
    }

    return results;
  }

  @Override
  public ArrayDeque<File> scanPictures(final String input) throws IOException {

    final Collection<File> files =
        FileUtils.listFiles(new File(input), TrueFileFilter.INSTANCE, null);

    final ArrayDeque<File> results = new ArrayDeque<>(files.size());

    for (final File file : files) {
      final String mimeType = Files.probeContentType(file.toPath());

      //noinspection UnstableApiUsage
      if (StringUtils.isNoneEmpty(mimeType)
          && MediaType.parse(mimeType).is(MediaType.ANY_IMAGE_TYPE)) {
        results.add(file);
      }
    }

    return results;
  }
}
