/* Licensed under Apache-2.0 */
package io.terrible.batch.directory.schedulers;

import io.terrible.batch.data.domain.Directory;
import io.terrible.batch.data.repository.DirectoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Slf4j
@Component
@EnableScheduling
public class DirectoryScheduler {

  private final SimpleJobLauncher launcher;

  private final Job job;

  private final DirectoryRepository directoryRepository;

  @Value("${batch.directory.default}")
  private String defaultDirectory;

  public DirectoryScheduler(
      final DirectoryRepository directoryRepository,
      final SimpleJobLauncher launcher,
      @Qualifier("io.terrible.batch.directory.jobs.directoryScannerJob") final Job job) {

    this.directoryRepository = directoryRepository;
    this.launcher = launcher;
    this.job = job;
  }

  @Scheduled(fixedDelayString = "${batch.directory.delay}")
  public void schedule() {

    getDirectoriesWithDefault().forEach(this::run);
  }

  private void run(final Directory directory) {
    final JobParameters jobParameters =
        new JobParametersBuilder()
            .addDate("date", new Date())
            .addString("directory", directory.getPath())
            .toJobParameters();

    try {
      launcher.run(job, jobParameters);
    } catch (final JobExecutionAlreadyRunningException
        | JobRestartException
        | JobInstanceAlreadyCompleteException
        | JobParametersInvalidException e) {
      log.error(
          "Error processing directory {} with message {}", directory.getPath(), e.getMessage(), e);
    }
  }

  private List<Directory> getDirectoriesWithDefault() {
    final List<Directory> directories = directoryRepository.findAll();

    if (directories.isEmpty()) {
      directoryRepository.save(Directory.builder().path(defaultDirectory).build());
    }

    return directories;
  }
}
