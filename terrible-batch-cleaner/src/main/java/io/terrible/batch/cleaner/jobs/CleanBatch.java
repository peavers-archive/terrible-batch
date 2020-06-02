/* Licensed under Apache-2.0 */
package io.terrible.batch.cleaner.jobs;

import io.terrible.batch.cleaner.processors.CleanProcessor;
import io.terrible.batch.data.domain.MediaFile;
import io.terrible.batch.data.repository.MediaFileRepository;
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

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class CleanBatch {

  private final JobBuilderFactory jobBuilderFactory;

  private final StepBuilderFactory stepBuilderFactory;

  private final MongoTemplate mongoTemplate;

  private final MediaFileRepository mediaFileRepository;

  @StepScope
  @Bean(name = "cleanReader")
  public ItemReader<MediaFile> reader() {

    final MongoItemReader<MediaFile> reader = new MongoItemReader<>();

    final Map<String, Sort.Direction> map = new HashMap<>();
    map.put("_id", Sort.Direction.DESC);

    reader.setTemplate(mongoTemplate);
    reader.setSort(map);
    reader.setTargetType(MediaFile.class);
    reader.setQuery("{}");
    reader.setSaveState(false);

    return reader;
  }

  @Bean(name = "cleanProcessor")
  public CleanProcessor processor() {

    return new CleanProcessor(mediaFileRepository);
  }

  @Bean(name = "cleanWriter")
  public ItemWriter<MediaFile> writer() {

    log.info("Writing for cleaner");

    final MongoItemWriter<MediaFile> writer = new MongoItemWriter<>();
    writer.setCollection("media-files");
    writer.setTemplate(mongoTemplate);

    return writer;
  }

  @Bean(name = "cleanStep")
  public Step cleanStep() {

    return stepBuilderFactory
        .get("cleanStep")
        .<MediaFile, MediaFile>chunk(1)
        .reader(reader())
        .writer(writer())
        .processor(processor())
        .build();
  }

  @Bean(name = "cleanJob")
  public Job directoryScannerJob() {

    return jobBuilderFactory
        .get("cleanJob")
        .incrementer(new RunIdIncrementer())
        .start(cleanStep())
        .build();
  }
}
