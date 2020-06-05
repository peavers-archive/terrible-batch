/* Licensed under Apache-2.0 */
package io.terrible.batch.configuration;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

@Configuration
public class DataSourceConfig {

  @Bean
  @Primary
  public DataSource hsqldbDataSource() {
    final SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
    dataSource.setDriver(new org.hsqldb.jdbc.JDBCDriver());
    dataSource.setUrl("jdbc:hsqldb:mem:testdb;sql.enforce_strict_size=true;hsqldb.tx=mvcc");
    dataSource.setUsername("sa");
    dataSource.setPassword("");

    return dataSource;
  }

  @Bean
  public JdbcTemplate jdbcTemplate(final DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }
}
