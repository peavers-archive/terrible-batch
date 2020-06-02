/* Licensed under Apache-2.0 */
package io.terrible.batch.cleaner.schedulers;

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

import java.util.Date;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class CleanScheduler {

  private final SimpleJobLauncher simpleJobLauncher;

  @Qualifier("cleanJob")
  private final Job cleanJob;

  @Scheduled(fixedDelayString = "${batch.cleaner.delay}")
  public void schedule() {

    execute();
  }

  private void execute() {

    final JobParameters jobParameters =
        new JobParametersBuilder().addDate("date", new Date()).toJobParameters();

    try {
      simpleJobLauncher.run(cleanJob, jobParameters);
    } catch (Exception e) {
      log.error("Unable to run {} {} {}", cleanJob.getName(), e.getMessage(), e);
    }
  }
}
