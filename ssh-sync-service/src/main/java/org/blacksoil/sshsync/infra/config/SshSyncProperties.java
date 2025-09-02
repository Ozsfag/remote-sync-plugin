package org.blacksoil.sshsync.infra.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ssh-sync")
@Data
public class SshSyncProperties {
  private int port;
  private int timeoutMs;
  private int scpBufferSize;
  private int ackWaitIterations;
  private int ackWaitIntervalMs;
  private boolean strictHostKeyChecking;
  private String preferredAuthentications;
}
