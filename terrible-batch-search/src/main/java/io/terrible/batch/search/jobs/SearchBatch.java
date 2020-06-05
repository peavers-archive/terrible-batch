/* Licensed under Apache-2.0 */
package io.terrible.batch.search.jobs;

import io.terrible.batch.data.domain.MediaFile;
import io.terrible.batch.search.listeners.SearchJobListener;
import io.terrible.batch.search.processors.SearchProcessor;
import io.terrible.batch.search.services.SearchService;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@Configuration
@EnableScheduling
@EnableBatchProcessing
@RequiredArgsConstructor
public class SearchBatch {

  private final JobBuilderFactory jobBuilderFactory;

  private final StepBuilderFactory stepBuilderFactory;

  private final MongoTemplate mongoTemplate;

  private final SearchService searchService;

  @Bean(name = "io.terrible.batch.search.jobs.reader")
  @StepScope
  public ItemReader<MediaFile> reader() {

    final MongoItemReader<MediaFile> reader = new MongoItemReader<>();

    final Map<String, Sort.Direction> map = new HashMap<>();
    map.put("_id", Sort.Direction.DESC);

    reader.setTemplate(mongoTemplate);
    reader.setSort(map);
    reader.setTargetType(MediaFile.class);
    reader.setQuery("{ isIndexed: false }");
    reader.setSaveState(true);

    return reader;
  }

  @Bean(name = "io.terrible.batch.search.jobs.processor")
  public SearchProcessor processor() {
    return new SearchProcessor(searchService);
  }

  @Bean(name = "io.terrible.batch.search.jobs.writer")
  public ItemWriter<MediaFile> writer() {

    final MongoItemWriter<MediaFile> writer = new MongoItemWriter<>();
    writer.setCollection("media-files");
    writer.setTemplate(mongoTemplate);

    return writer;
  }

  @Bean(name = "io.terrible.batch.search.jobs.searchStep")
  public Step searchStep() {

    return stepBuilderFactory
        .get("searchStep")
        .<MediaFile, MediaFile>chunk(100)
        .reader(reader())
        .processor(processor())
        .writer(writer())
        .build();
  }

  @Bean(name = "io.terrible.batch.search.jobs.searchJob")
  public Job searchJob() {

    return jobBuilderFactory
        .get("searchJob")
        .listener(new SearchJobListener(searchService))
        .incrementer(new RunIdIncrementer())
        .flow(searchStep())
        .end()
        .build();
  }
}
