package com.merchantsledger.config;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

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
    boolean renderEnv = "true".equalsIgnoreCase(environment.getProperty("RENDER"));
    String url = firstNonBlank(
        environment.getProperty("SPRING_DATASOURCE_URL"),
        environment.getProperty("DB_URL"),
        environment.getProperty("DATABASE_URL"),
        environment.getProperty("POSTGRES_URL"),
        properties.getUrl()
    );
    if (isPlaceholderUrl(url) || (renderEnv && url != null && url.contains("localhost:5432"))) {
      url = null;
    }

    if (url == null || url.isBlank()) {
      String host = firstNonBlank(environment.getProperty("DB_HOST"), environment.getProperty("POSTGRES_HOST"), environment.getProperty("PGHOST"));
      String port = firstNonBlank(environment.getProperty("DB_PORT"), environment.getProperty("POSTGRES_PORT"), environment.getProperty("PGPORT"), "5432");
      String db = firstNonBlank(environment.getProperty("DB_NAME"), environment.getProperty("POSTGRES_DB"), environment.getProperty("PGDATABASE"));
      if (isPlaceholderHostPortDb(host, port, db)) {
        throw new IllegalStateException("Render DB env vars use placeholders. Set DB_HOST/DB_PORT/DB_NAME from database values.");
      }
      if (host != null && db != null) {
        url = "jdbc:postgresql://" + host + ":" + port + "/" + db;
      }
    }

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

    String explicitUser = firstNonBlank(
        environment.getProperty("SPRING_DATASOURCE_USERNAME"),
        environment.getProperty("DB_USERNAME"),
        environment.getProperty("DATABASE_USERNAME"),
        environment.getProperty("POSTGRES_USER"),
        environment.getProperty("PGUSER")
    );
    String explicitPass = firstNonBlank(
        environment.getProperty("SPRING_DATASOURCE_PASSWORD"),
        environment.getProperty("DB_PASSWORD"),
        environment.getProperty("DATABASE_PASSWORD"),
        environment.getProperty("POSTGRES_PASSWORD"),
        environment.getProperty("PGPASSWORD")
    );

    if (url != null && url.startsWith("jdbc:postgresql://") && url.contains("@")) {
      ParsedJdbcUrl parsed = parseJdbcUrlWithUserInfo(url);
      if (parsed != null) {
        url = parsed.jdbcUrl;
        if (isBlank(explicitUser) && !isBlank(parsed.username)) {
          properties.setUsername(parsed.username);
        }
        if (isBlank(explicitPass) && !isBlank(parsed.password)) {
          properties.setPassword(parsed.password);
        }
      }
    }

    if (renderEnv && (url == null || url.isBlank())) {
      throw new IllegalStateException(
          "Database URL is not configured on Render. Set DB_HOST/DB_PORT/DB_NAME and DB_USERNAME/DB_PASSWORD.");
    }
    if (url != null && !url.isBlank()) {
      properties.setUrl(url);
    }
    if (!isBlank(explicitUser)) {
      properties.setUsername(explicitUser);
    }
    if (!isBlank(explicitPass)) {
      properties.setPassword(explicitPass);
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

  private boolean isBlank(String value) {
    return value == null || value.isBlank();
  }

  private boolean isPlaceholderUrl(String url) {
    if (isBlank(url)) {
      return false;
    }
    String normalized = url.trim().toLowerCase();
    return normalized.equals("jdbc:postgresql://host:port/database")
        || normalized.equals("postgresql://host:port/database")
        || normalized.equals("postgres://host:port/database");
  }

  private boolean isPlaceholderHostPortDb(String host, String port, String db) {
    return "host".equalsIgnoreCase(String.valueOf(host))
        || "port".equalsIgnoreCase(String.valueOf(port))
        || "database".equalsIgnoreCase(String.valueOf(db));
  }

  private ParsedJdbcUrl parseJdbcUrlWithUserInfo(String jdbcUrl) {
    try {
      URI uri = URI.create(jdbcUrl.substring("jdbc:".length()));
      String host = uri.getHost();
      String db = uri.getPath() == null ? null : uri.getPath().replaceFirst("^/", "");
      int port = uri.getPort() > 0 ? uri.getPort() : 5432;
      if (isBlank(host) || isBlank(db)) {
        return null;
      }

      String username = null;
      String password = null;
      String userInfo = uri.getUserInfo();
      if (!isBlank(userInfo)) {
        String[] parts = userInfo.split(":", 2);
        username = decode(parts[0]);
        if (parts.length > 1) {
          password = decode(parts[1]);
        }
      }

      return new ParsedJdbcUrl("jdbc:postgresql://" + host + ":" + port + "/" + db, username, password);
    } catch (Exception ignored) {
      return null;
    }
  }

  private String decode(String value) {
    return URLDecoder.decode(value, StandardCharsets.UTF_8);
  }

  private static class ParsedJdbcUrl {
    private final String jdbcUrl;
    private final String username;
    private final String password;

    private ParsedJdbcUrl(String jdbcUrl, String username, String password) {
      this.jdbcUrl = jdbcUrl;
      this.username = username;
      this.password = password;
    }
  }
}
