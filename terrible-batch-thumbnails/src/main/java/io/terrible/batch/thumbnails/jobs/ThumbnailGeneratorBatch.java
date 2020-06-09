/* Licensed under Apache-2.0 */
package io.terrible.batch.thumbnails.jobs;

import io.terrible.batch.data.domain.MediaFile;
import io.terrible.batch.thumbnails.processors.ThumbnailProcessor;
import io.terrible.batch.thumbnails.services.ThumbnailService;
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
import org.springframework.batch.core.partition.support.SimplePartitioner;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@Configuration
@EnableScheduling
@EnableBatchProcessing
@RequiredArgsConstructor
public class ThumbnailGeneratorBatch {

  private final ThumbnailService thumbnailService;

  private final JobBuilderFactory jobBuilderFactory;

  private final StepBuilderFactory stepBuilderFactory;

  private final MongoTemplate mongoTemplate;

  @StepScope
  @Bean(name = "io.terrible.batch.thumbnails.jobs.reader")
  public ItemReader<MediaFile> reader() {

    final MongoItemReader<MediaFile> reader = new MongoItemReader<>();

    final Map<String, Sort.Direction> map = new HashMap<>();
    map.put("_id", Sort.Direction.DESC);

    reader.setTemplate(mongoTemplate);
    reader.setSort(map);
    reader.setTargetType(MediaFile.class);
    reader.setQuery("{ 'thumbnails.11': { $exists: false }, isIgnored: false }");
    reader.setSaveState(false);

    return reader;
  }

  @Bean(name = "io.terrible.batch.thumbnails.jobs.processor")
  public ThumbnailProcessor processor() {

    return new ThumbnailProcessor(thumbnailService);
  }

  @Bean(name = "io.terrible.batch.thumbnails.jobs.writer")
  public ItemWriter<MediaFile> writer() {

    final MongoItemWriter<MediaFile> writer = new MongoItemWriter<>();
    writer.setCollection("media-files");
    writer.setTemplate(mongoTemplate);

    return writer;
  }

  @Bean(name = "io.terrible.batch.thumbnails.jobs.thumbnailGeneratorJob")
  public Job thumbnailGeneratorJob() {

    return jobBuilderFactory
        .get("thumbnailGeneratorJob")
        .incrementer(new RunIdIncrementer())
        .flow(partitionedStep(thumbnailGeneratorStep()))
        .end()
        .build();
  }

  @Bean(name = "io.terrible.batch.thumbnails.jobs.thumbnailGeneratorStep")
  public Step thumbnailGeneratorStep() {

    return stepBuilderFactory
        .get("thumbnailGeneratorStep")
        .<MediaFile, MediaFile>chunk(6)
        .reader(reader())
        .processor(processor())
        .writer(writer())
        .taskExecutor(taskExecutor())
        .build();
  }

  @Bean(name = "io.terrible.batch.thumbnails.jobs.partitionedStep")
  public Step partitionedStep(
      @Qualifier("io.terrible.batch.thumbnails.jobs.thumbnailGeneratorStep")
          final Step thumbnailGeneratorStep) {

    return stepBuilderFactory
        .get("partitionedStep")
        .partitioner(thumbnailGeneratorStep)
        .partitioner("thumbnailGeneratorStep", new SimplePartitioner())
        .gridSize(6)
        .build();
  }

  @Bean
  public TaskExecutor taskExecutor() {
    return new SimpleAsyncTaskExecutor("thumbnail_task");
  }
}
