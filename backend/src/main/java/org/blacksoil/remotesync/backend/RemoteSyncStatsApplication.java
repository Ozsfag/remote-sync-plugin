package org.blacksoil.remotesync.backend; // замени на свой пакет

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
  org.blacksoil.remotesync.backend.properties.MarketplaceProperties.class,
  org.blacksoil.remotesync.backend.properties.CommunityProperties.class
})
public class RemoteSyncStatsApplication {
  public static void main(String[] args) {
    SpringApplication.run(RemoteSyncStatsApplication.class, args);
  }
}
