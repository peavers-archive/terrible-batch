/* Licensed under Apache-2.0 */
package io.terrible.batch.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class SimpleJobConfig {

  private final JobRepository jobRepository;

  @Bean
  public SimpleJobLauncher simpleJobLauncher() throws Exception {

    final SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
    jobLauncher.setJobRepository(jobRepository);
    jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor("terrible-batch-"));
    jobLauncher.afterPropertiesSet();

    return jobLauncher;
  }
}
