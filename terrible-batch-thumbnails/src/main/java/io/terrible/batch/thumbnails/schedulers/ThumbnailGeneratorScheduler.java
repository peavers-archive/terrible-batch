/* Licensed under Apache-2.0 */
package io.terrible.batch.thumbnails.schedulers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
@EnableScheduling
public class ThumbnailGeneratorScheduler {

  private final SimpleJobLauncher launcher;

  private final Job job;

  public ThumbnailGeneratorScheduler(
      final SimpleJobLauncher launcher,
      @Qualifier("io.terrible.batch.thumbnails.jobs.thumbnailGeneratorJob") final Job job) {

    this.launcher = launcher;
    this.job = job;
  }

  @Scheduled(fixedDelayString = "${batch.thumbnails.delay}")
  public void schedule()
      throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
          JobRestartException, JobInstanceAlreadyCompleteException {

    launcher.run(job, new JobParametersBuilder().addDate("date", new Date()).toJobParameters());
  }
}
