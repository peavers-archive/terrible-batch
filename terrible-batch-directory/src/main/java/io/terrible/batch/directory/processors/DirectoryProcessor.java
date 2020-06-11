/* Licensed under Apache-2.0 */
package io.terrible.batch.directory.processors;

import io.terrible.batch.data.domain.MediaFile;
import io.terrible.batch.data.repository.MediaFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;

@Slf4j
@RequiredArgsConstructor
public class DirectoryProcessor implements ItemProcessor<MediaFile, MediaFile> {

  private final MediaFileRepository mediaFileRepository;

  @Override
  public MediaFile process(@NonNull final MediaFile input) {
    final MediaFile lookup =
        mediaFileRepository.findById(input.getId()).orElse(MediaFile.builder().build());

    lookup.setId(input.getId());
    lookup.setName(input.getName());
    lookup.setPath(input.getPath());
    lookup.setExtension(input.getExtension());
    lookup.setSize(input.getSize());
    lookup.setCreatedTime(input.getCreatedTime());
    lookup.setLastAccessTime(input.getLastAccessTime());
    lookup.setLastModifiedTime(input.getLastModifiedTime());
    lookup.setLastWatched(input.getLastWatched());

    return lookup;
  }
}
