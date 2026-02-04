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
    String url = firstNonBlank(
        environment.getProperty("DB_URL"),
        environment.getProperty("DATABASE_URL"),
        environment.getProperty("POSTGRES_INTERNAL_URL"),
        environment.getProperty("POSTGRES_URL"),
        properties.getUrl()
    );
    if (url == null || url.isBlank()) {
      String host = firstNonBlank(environment.getProperty("POSTGRES_HOST"), environment.getProperty("PGHOST"));
      String port = firstNonBlank(environment.getProperty("POSTGRES_PORT"), environment.getProperty("PGPORT"), "5432");
      String db = firstNonBlank(environment.getProperty("POSTGRES_DB"), environment.getProperty("PGDATABASE"));
      if (host != null && db != null) {
        url = "jdbc:postgresql://" + host + ":" + port + "/" + db;
      }
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

  private String firstNonBlank(String... candidates) {
    for (String value : candidates) {
      if (value != null && !value.isBlank()) {
        return value;
      }
    }
    return null;
  }
}
