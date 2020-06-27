package io.terrible.batch.thumbnails.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

/**
 * I want async jobs running, however I don't want more than one instances of the heavy long running
 * jobs. This listener creates a very simple lock mechanism to prevent multiple instances being ran
 * concurrently while letting other jobs run asynchronously.
 */
@Slf4j
public class JobLockExecutionListener implements JobExecutionListener {

  private JobExecution active;

  /** Create a lock */
  @Override
  public void beforeJob(final JobExecution jobExecution) {
    synchronized (jobExecution) {
      if (active != null && active.isRunning()) {
        log.info("Job already running, skipping this execution");
        jobExecution.stop();
      } else {
        active = jobExecution;
      }
    }
  }

  /** Remove the lock */
  @Override
  public void afterJob(final JobExecution jobExecution) {
    synchronized (jobExecution) {
      if (jobExecution == active) {
        active = null;
      }
    }
  }
}
