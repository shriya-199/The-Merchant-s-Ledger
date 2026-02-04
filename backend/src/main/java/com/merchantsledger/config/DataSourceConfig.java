package com.merchantsledger.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

@Configuration
public class DataSourceConfig {
  @Bean
  @Primary
  public DataSource dataSource(DataSourceProperties properties, Environment environment) {
    String url = properties.getUrl();
    if (url == null || url.isBlank()) {
      url = environment.getProperty("DB_URL");
    }
    if (url == null || url.isBlank()) {
      url = environment.getProperty("DATABASE_URL");
    }
    if (url != null && url.startsWith("postgres://")) {
      url = "jdbc:postgresql://" + url.substring("postgres://".length());
    } else if (url != null && url.startsWith("postgresql://")) {
      url = "jdbc:postgresql://" + url.substring("postgresql://".length());
    }
    if (url != null && !url.isBlank()) {
      properties.setUrl(url);
    }
    return properties.initializeDataSourceBuilder().build();
  }
}
