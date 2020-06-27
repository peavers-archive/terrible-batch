/* Licensed under Apache-2.0 */
package io.terrible.batch.search.services;

import io.terrible.batch.data.domain.MediaFile;
import io.terrible.batch.search.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

  private final BulkProcessor bulkProcessor;

  private final RestHighLevelClient client;

  @Override
  public void createIndex(final String index) {

    log.info("Attempting to create index {}", index);

    if (isExistingIndex(index, client)) {
      log.info("Index {} already exists, returning", index);
      return;
    }

    final String indexSettings = getSettings();

    if (StringUtils.isNotEmpty(indexSettings)) {
      final CreateIndexRequest createIndexRequest = new CreateIndexRequest(index);
      createIndexRequest.source(indexSettings, XContentType.JSON);

      try {
        client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        client
            .cluster()
            .health(new ClusterHealthRequest(index).waitForYellowStatus(), RequestOptions.DEFAULT);
      } catch (final Exception e) {
        log.warn("Unable to create index {}", e.getMessage());
      }
    }
  }

  @Override
  public void addToIndex(final String index, final MediaFile mediaFile) {

    bulkProcessor.add(
        new IndexRequest(index)
            .id(mediaFile.getId())
            .source(JsonUtils.toJson(mediaFile), XContentType.JSON));
  }

  @Override
  public void flushIndex() {
    bulkProcessor.flush();
  }

  private String getSettings() {
    try {
      final InputStream stream = new ClassPathResource("es_settings.json").getInputStream();

      return IOUtils.toString(stream, Charset.defaultCharset());
    } catch (final IOException e) {
      log.error("Unable to get search settings file {}", e.getMessage());
      return null;
    }
  }

  private boolean isExistingIndex(final String index, final RestHighLevelClient client) {

    try {
      return client.indices().exists(new GetIndexRequest(index), RequestOptions.DEFAULT);
    } catch (final IOException e) {
      return true;
    }
  }
}
