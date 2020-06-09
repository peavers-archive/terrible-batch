/* Licensed under Apache-2.0 */
package io.terrible.batch.directory.schedulers;

import io.terrible.batch.data.domain.Directory;
import io.terrible.batch.data.repository.DirectoryRepository;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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

    final Directory directory = getDirectory();

    if (StringUtils.isEmpty(directory.getPath())) {
      log.warn("No directory found, skipping job");
    } else {
      final JobParameters jobParameters =
          new JobParametersBuilder()
              .addDate("date", new Date())
              .addString("directory", directory.getPath())
              .toJobParameters();

      try {
        launcher.run(job, jobParameters);
      } catch (final Exception e) {
        log.error("Unable to run {} {} {}", job.getName(), e.getMessage(), e);
      }
    }
  }

  private Directory getDirectory() {
    final List<Directory> directories = directoryRepository.findAll();

    return directories.isEmpty()
        ? directoryRepository.save(Directory.builder().path(defaultDirectory).build())
        : directories.get(0);
  }
}
