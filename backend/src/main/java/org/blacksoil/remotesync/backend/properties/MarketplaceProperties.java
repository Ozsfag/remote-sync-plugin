package org.blacksoil.remotesync.backend.properties;

import java.time.Duration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "marketplace")
@Data
public class MarketplaceProperties {

  private String baseUrl;
  private Duration requestTimeout;
  private Duration cacheTtl;
  private String userAgent;
  private boolean preferXmlIdLink;
}
