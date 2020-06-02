/* Licensed under Apache-2.0 */
package io.terrible.batch.directory.schedulers;

import io.terrible.batch.data.domain.Directory;
import io.terrible.batch.data.repository.DirectoryRepository;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class DirectoryScheduler {

  private static final String DEFAULT_DIRECTORY = "/terrible/terrible-media";

  private final DirectoryRepository directoryRepository;

  private final SimpleJobLauncher simpleJobLauncher;

  @Qualifier("directoryScannerJob")
  private final Job directoryScannerJob;

  @Scheduled(fixedDelayString = "${batch.directory.delay}")
  public void schedule() {

    final Directory directory = getDirectory();

    execute(directory);
  }

  private void execute(Directory directory) {

    final JobParameters jobParameters =
        new JobParametersBuilder()
            .addDate("date", new Date())
            .addString("directory", directory.getPath())
            .toJobParameters();

    try {
      simpleJobLauncher.run(directoryScannerJob, jobParameters);
    } catch (Exception e) {
      log.error("Unable to run {} {} {}", directoryScannerJob.getName(), e.getMessage(), e);
    }
  }

  private Directory getDirectory() {
    final List<Directory> directories = directoryRepository.findAll();

    return directories.isEmpty()
        ? directoryRepository.save(Directory.builder().path(DEFAULT_DIRECTORY).build())
        : directories.get(0);
  }
}
