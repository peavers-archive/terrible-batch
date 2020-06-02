/* Licensed under Apache-2.0 */
package io.terrible.batch.directory.jobs;

import io.terrible.batch.data.domain.MediaFile;
import io.terrible.batch.data.repository.MediaFileRepository;
import io.terrible.batch.directory.processors.DirectoryProcessor;
import io.terrible.batch.directory.services.ScanService;
import io.terrible.batch.directory.tasklet.SearchIndexTasklet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class DirectoryBatch {

  private final JobBuilderFactory jobBuilderFactory;

  private final StepBuilderFactory stepBuilderFactory;

  private final MongoTemplate mongoTemplate;

  private final ScanService scanService;

  private final MediaFileRepository mediaFileRepository;

  @StepScope
  @Bean(name = "directoryScanReader")
  public ItemReader<MediaFile> reader(@Value("#{jobParameters['directory']}") final String dir) {

    return new IteratorItemReader<>(scanService.scanVideos(dir));
  }

  @Bean(name = "directoryScanProcessor")
  public DirectoryProcessor processor() {

    return new DirectoryProcessor(mediaFileRepository);
  }

  @Bean(name = "directoryScanWriter")
  public ItemWriter<MediaFile> writer() {

    final MongoItemWriter<MediaFile> writer = new MongoItemWriter<>();
    writer.setCollection("media-files");
    writer.setTemplate(mongoTemplate);

    return writer;
  }

  @Bean(name = "directoryScannerStep")
  public Step directoryScannerStep() {

    return stepBuilderFactory
        .get("directoryScannerStep")
        .<MediaFile, MediaFile>chunk(1)
        .reader(reader(StringUtils.EMPTY))
        .processor(processor())
        .writer(writer())
        .build();
  }

  @Bean(name = "searchIndexStep")
  public Step searchIndexStep() {

    return stepBuilderFactory.get("searchIndexStep").tasklet(new SearchIndexTasklet()).build();
  }

  @Bean(name = "directoryScannerJob")
  public Job directoryScannerJob() {

    return jobBuilderFactory
        .get("directoryScannerJob")
        .incrementer(new RunIdIncrementer())
        .start(directoryScannerStep())
        .next(searchIndexStep())
        .build();
  }
}
