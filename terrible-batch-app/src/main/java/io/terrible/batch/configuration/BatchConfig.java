/* Licensed under Apache-2.0 */
package io.terrible.batch.configuration;

import javax.sql.DataSource;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/** @author Chris Turner (chris@forloop.space) */
@Configuration
@EnableAutoConfiguration
@EnableBatchProcessing
public class BatchConfig extends DefaultBatchConfigurer {

  @Override
  public void setDataSource(final DataSource dataSource) {}
}
