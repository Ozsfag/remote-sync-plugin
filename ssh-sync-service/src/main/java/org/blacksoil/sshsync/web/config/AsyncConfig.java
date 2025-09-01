package org.blacksoil.sshsync.web.config;

import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {

  @Bean
  public AsyncTaskExecutor applicationTaskExecutor() {
    ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
    ex.setThreadNamePrefix("ssh-sync-");
    ex.setCorePoolSize(4);
    ex.setMaxPoolSize(16);
    ex.setQueueCapacity(200);
    ex.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    ex.initialize();
    return ex;
  }
}
