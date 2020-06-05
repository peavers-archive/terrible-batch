/* Licensed under Apache-2.0 */
package io.terrible.batch.directory.tasklet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
public class SearchIndexTasklet implements Tasklet {

  private final String searchEndpoint;

  public SearchIndexTasklet(final String searchEndpoint) {

    this.searchEndpoint = searchEndpoint;
  }

  @Override
  public RepeatStatus execute(
      final StepContribution contribution, final ChunkContext chunkContext) {

    try {
      HttpClient.newHttpClient()
          .send(
              HttpRequest.newBuilder()
                  .uri(URI.create(searchEndpoint + "/task/search/reindex"))
                  .build(),
              HttpResponse.BodyHandlers.ofString());
    } catch (IOException | InterruptedException e) {
      log.warn("Unable to start search reindex {}", e.getMessage());
    }

    // We don't care about the outcome #yolo
    return null;
  }
}
