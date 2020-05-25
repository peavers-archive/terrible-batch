/* Licensed under Apache-2.0 */
package io.terrible.batch.directory.schedulers;

import io.terrible.batch.data.domain.Directory;
import io.terrible.batch.data.repository.DirectoryRepository;
import java.util.Date;
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
public class DirectoryScanScheduler {

  private final DirectoryRepository directoryRepository;

  private final SimpleJobLauncher simpleJobLauncher;

  @Qualifier("directoryScannerJob")
  private final Job directoryScannerJob;

  @Scheduled(fixedDelayString = "${batch.delay}")
  public void schedule() {

    final Directory directory = directoryRepository.findAll().get(0);

    if (directory != null) {
      execute(directory);
    }
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
}
