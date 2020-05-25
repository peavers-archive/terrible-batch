/* Licensed under Apache-2.0 */
package io.terrible.batch.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SimpleJobConfig {

  private final JobRepository jobRepository;

  @Bean
  public SimpleJobLauncher simpleJobLauncher() {

    SimpleJobLauncher simpleJobLauncher = new SimpleJobLauncher();
    simpleJobLauncher.setJobRepository(jobRepository);

    return simpleJobLauncher;
  }
}
