package org.blacksoil.sshsync.app.web.jobs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jobs")
public class JobProperties {
    private int poolSize;
    private long sseTimeoutMs;
    private long cleanupIntervalMs;
    private long ttlMs;
}
