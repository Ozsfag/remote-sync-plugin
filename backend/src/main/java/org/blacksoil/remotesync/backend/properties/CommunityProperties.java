package org.blacksoil.remotesync.backend.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "community")
public class CommunityProperties {

  private Integer sponsorsCount;
  private String baselineFile;
}
