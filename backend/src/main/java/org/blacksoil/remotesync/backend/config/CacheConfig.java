package org.blacksoil.remotesync.backend.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.blacksoil.remotesync.backend.dto.MarketplaceStatsRecord;
import org.blacksoil.remotesync.backend.properties.MarketplaceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

  @Bean
  public Cache<String, MarketplaceStatsRecord> pluginStatsCache(MarketplaceProperties props) {
    long ttlMs = props.getCacheTtl().toMillis();
    return Caffeine.newBuilder()
        .expireAfterWrite(ttlMs, TimeUnit.MILLISECONDS)
        .maximumSize(1_000)
        .build();
  }
}
