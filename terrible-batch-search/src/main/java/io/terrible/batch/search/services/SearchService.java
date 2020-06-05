/* Licensed under Apache-2.0 */
package io.terrible.batch.search.services;

import io.terrible.batch.data.domain.MediaFile;

import java.io.IOException;

public interface SearchService {

  void createIndex(String index) throws IOException;

  void addToIndex(String index, MediaFile mediaFile);

  void flushIndex();
}
