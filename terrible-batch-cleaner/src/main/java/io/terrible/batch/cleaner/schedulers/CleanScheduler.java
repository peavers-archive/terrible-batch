/* Licensed under Apache-2.0 */
package io.terrible.batch.cleaner.schedulers;

import java.util.Date;
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
public class CleanScheduler {

  private final SimpleJobLauncher launcher;

  private final Job job;

  public CleanScheduler(
      final SimpleJobLauncher launcher,
      @Qualifier("io.terrible.batch.cleaner.jobs" + ".cleanJob") final Job job) {

    this.launcher = launcher;
    this.job = job;
  }

  @Scheduled(fixedDelayString = "${batch.cleaner.delay}")
  public void schedule() {
    final JobParameters jobParameters =
        new JobParametersBuilder().addDate("date", new Date()).toJobParameters();

    try {
      launcher.run(job, jobParameters);
    } catch (final Exception e) {
      log.error("Unable to run {} {} {}", job.getName(), e.getMessage(), e);
    }
  }
}
