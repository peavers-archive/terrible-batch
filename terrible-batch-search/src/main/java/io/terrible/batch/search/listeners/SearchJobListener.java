package io.terrible.batch.search.listeners;

import io.terrible.batch.search.services.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchJobListener implements JobExecutionListener {

  private final SearchService searchService;

  @Override
  public void beforeJob(final JobExecution jobExecution) {

    try {
      searchService.createIndex("media-files");
    } catch (IOException e) {
      log.error("Unable to create index {}", e.getMessage());
    }
  }

  @Override
  public void afterJob(final JobExecution jobExecution) {
    searchService.flushIndex();
  }
}
