/* Licensed under Apache-2.0 */
package io.terrible.batch.processors;

import io.terrible.batch.domain.MediaFile;
import io.terrible.batch.repository.MediaFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.BeanUtils;
import org.springframework.lang.NonNull;

@Slf4j
@RequiredArgsConstructor
public class DirectoryProcessor implements ItemProcessor<MediaFile, MediaFile> {

  private final MediaFileRepository mediaFileRepository;

  @Override
  public MediaFile process(@NonNull final MediaFile input) {

    final MediaFile output =
        mediaFileRepository.findById(input.getId()).orElse(MediaFile.builder().build());

    BeanUtils.copyProperties(input, output, "thumbnailPath", "thumbnails");

    return output;
  }
}
