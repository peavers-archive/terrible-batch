/* Licensed under Apache-2.0 */
package io.terrible.batch.search.processors;

import io.terrible.batch.data.domain.MediaFile;
import io.terrible.batch.search.services.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;

@Slf4j
@RequiredArgsConstructor
public class SearchProcessor implements ItemProcessor<MediaFile, MediaFile> {

  private static final String INDEX = "media-files";

  private final SearchService searchService;

  @Override
  public MediaFile process(@NonNull final MediaFile mediaFile) {

    searchService.addToIndex(INDEX, mediaFile);
    mediaFile.setIndexed(true);

    return mediaFile;
  }
}
