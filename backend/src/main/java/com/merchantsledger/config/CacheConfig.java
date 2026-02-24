package com.merchantsledger.config;

import java.time.Duration;

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

@Configuration
public class CacheConfig implements CachingConfigurer {
  @Bean
  public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
    return (builder) -> builder
        .withCacheConfiguration("inventoryStock", ttlOfSeconds(90))
        .withCacheConfiguration("inventoryMovements", ttlOfSeconds(45))
        .withCacheConfiguration("inventorySummary", ttlOfSeconds(60))
        .withCacheConfiguration("inventoryLowStock", ttlOfSeconds(30))
        .withCacheConfiguration("analytics", ttlOfSeconds(45))
        .withCacheConfiguration("summary", ttlOfSeconds(60));
  }

  @Override
  public CacheErrorHandler errorHandler() {
    return new CacheErrorHandler() {
      @Override
      public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        swallow(exception);
      }

      @Override
      public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
        swallow(exception);
      }

      @Override
      public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        swallow(exception);
      }

      @Override
      public void handleCacheClearError(RuntimeException exception, Cache cache) {
        swallow(exception);
      }

      private void swallow(RuntimeException exception) {
        if (exception instanceof DataAccessException) {
          return;
        }
        throw exception;
      }
    };
  }

  private RedisCacheConfiguration ttlOfSeconds(long seconds) {
    return RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofSeconds(seconds))
        .disableCachingNullValues();
  }
}
