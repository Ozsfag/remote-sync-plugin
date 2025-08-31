package org.blacksoil.sshsync.app.web.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.blacksoil.sshsync.app.web.jobs.config.JobProperties;
import org.blacksoil.sshsync.app.web.jobs.repo.JobRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobCleaner {

  private final JobProperties props;
  private final JobRepository repo;
  private final SseHub sse;

  @Scheduled(fixedDelayString = "#{@jobProperties.cleanupIntervalMs}")
  void cleanup() {
    // завершаем SSE перед удалением записей, чтобы не висели эмиттеры
    repo.findAll()
        .forEach(
            j -> {
              boolean done =
                  switch (j.getState()) {
                    case COMPLETED, FAILED, CANCELED -> true;
                    default -> false;
                  };
              if (done) sse.complete(j.getId());
            });
    int removed = repo.deleteExpired(props.getTtlMs());
    if (removed > 0) {
      log.debug("Jobs cleanup: removed {}", removed);
    }
  }
}
