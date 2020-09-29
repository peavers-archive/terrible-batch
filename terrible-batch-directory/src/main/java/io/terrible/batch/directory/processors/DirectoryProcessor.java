/* Licensed under Apache-2.0 */
package io.terrible.batch.directory.processors;

import io.terrible.batch.data.domain.MediaFile;
import io.terrible.batch.data.repository.MediaFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DirectoryProcessor {

  private final MediaFileRepository mediaFileRepository;

  public MediaFile process(@NonNull final MediaFile input) {
    final MediaFile mediaFile =
        mediaFileRepository.findById(input.getId()).orElse(MediaFile.builder().build());

    mediaFile.setId(input.getId());
    mediaFile.setName(input.getName());
    mediaFile.setPath(input.getPath());
    mediaFile.setExtension(input.getExtension());
    mediaFile.setSize(input.getSize());
    mediaFile.setCreatedTime(input.getCreatedTime());
    mediaFile.setLastAccessTime(input.getLastAccessTime());
    mediaFile.setLastModifiedTime(input.getLastModifiedTime());
    mediaFile.setLastWatched(input.getLastWatched());

    return mediaFileRepository.save(mediaFile);
  }
}
