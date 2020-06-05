/* Licensed under Apache-2.0 */
package io.terrible.batch.search.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.terrible.batch.data.domain.MediaFile;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.search.SearchHit;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JsonUtils {

  private static ObjectMapper objectMapper() {
    final ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.findAndRegisterModules();

    return objectMapper;
  }

  public static String toJson(final MediaFile mediaFileDto) {

    try {
      return objectMapper().writeValueAsString(mediaFileDto);
    } catch (final JsonProcessingException e) {
      log.error("Unable to parse to json {}", e.getMessage(), e);

      throw new RuntimeException(e.getMessage());
    }
  }

  public static MediaFile convertSourceMap(final SearchHit searchHit) {

    final MediaFile mediaFileDto =
        objectMapper().convertValue(searchHit.getSourceAsMap(), MediaFile.class);

    mediaFileDto.setId(searchHit.getId());

    return mediaFileDto;
  }
}
