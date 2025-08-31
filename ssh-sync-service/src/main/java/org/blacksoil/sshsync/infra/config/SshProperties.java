package org.blacksoil.sshsync.infra.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ssh")
public class SshProperties {
  private int port;
  private int timeoutMs;
  private boolean strictHostKeyChecking;
  private String preferredAuthentications;
}
