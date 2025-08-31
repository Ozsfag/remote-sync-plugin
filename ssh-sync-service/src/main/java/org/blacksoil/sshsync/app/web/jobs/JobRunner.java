// src/main/java/org/blacksoil/sshsync/app/web/jobs/JobRunner.java
package org.blacksoil.sshsync.app.web.jobs;

import jakarta.annotation.PreDestroy;
import java.util.concurrent.*;
import lombok.RequiredArgsConstructor;
import org.blacksoil.sshsync.app.web.jobs.config.JobProperties;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobRunner {

  private final JobProperties props;
  private final ExecutorService pool = newExecutor();

  public void submit(Runnable task) {
    pool.submit(task);
  }

  @PreDestroy
  void shutdown() {
    pool.shutdown();
  }

  private ExecutorService newExecutor() {
    int size =
        props.getPoolSize() > 0
            ? props.getPoolSize()
            : Math.max(2, Runtime.getRuntime().availableProcessors());
    ThreadFactory tf =
        r -> {
          Thread t = new Thread(r);
          t.setName("jobs-" + t.getId());
          t.setDaemon(true);
          return t;
        };
    return Executors.newFixedThreadPool(size, tf);
  }
}
