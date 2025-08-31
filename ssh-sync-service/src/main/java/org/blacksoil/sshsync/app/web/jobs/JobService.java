package org.blacksoil.sshsync.app.web.jobs;

import static org.blacksoil.sshsync.app.web.jobs.model.JobState.*;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.blacksoil.shareddto.sshsync.JobStatus;
import org.blacksoil.sshsync.app.web.jobs.mapper.JobStatusMapper;
import org.blacksoil.sshsync.app.web.jobs.model.JobWork;
import org.blacksoil.sshsync.app.web.jobs.repo.JobRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {

  private final JobRepository repo;
  private final JobRunner runner;
  private final SseHub sse;
  private final JobStatusMapper mapper;

  /** Создать задачу и вернуть id. */
  public String create() {
    return repo.create().getId();
  }

  /** Подписка на события (сразу шлём снапшот состояния). */
  public SseEmitter subscribe(String jobId) {
    // <-- У СЕБЯ ДОЛЖНО БЫТЬ ИМЕННО ДВА АРГУМЕНТА
    return sse.subscribe(jobId, mapper.jobToJobStatus(repo.get(jobId)));
  }

  /** Запуск фоновой работы. */
  public void submit(String jobId, JobWork work) {
    repo.setState(jobId, RUNNING);
    sse.broadcast(jobId, "state", Map.of("state", RUNNING.name()));

    runner.submit(
        () -> {
          try {
            work.run(new Sink(jobId));
            if (!repo.get(jobId).getCanceled().get()) {
              repo.setState(jobId, COMPLETED);
              sse.broadcast(jobId, "completed", Map.of("state", COMPLETED.name()));
            }
          } catch (Throwable t) {
            repo.setState(jobId, FAILED);
            sse.broadcast(
                jobId,
                "failed",
                Map.of("error", t.getMessage() == null ? "unknown" : t.getMessage()));
            log.warn("Job {} failed", jobId, t);
          } finally {
            sse.complete(jobId);
          }
        });
  }

  /** Ручное обновление прогресса. */
  public void update(String jobId, int progress, String message) {
    repo.setProgress(jobId, progress, message);
    sse.broadcast(
        jobId, "progress", Map.of("progress", progress, "message", message == null ? "" : message));
  }

  /** Отмена. */
  public void cancel(String jobId) {
    if (repo.cancel(jobId)) {
      repo.setState(jobId, CANCELED);
      sse.broadcast(jobId, "canceled", Map.of("state", CANCELED.name()));
      sse.complete(jobId);
    }
  }

  /** Текущий статус в shared-DTO. */
  public JobStatus status(String jobId) {
    return mapper.jobToJobStatus(repo.get(jobId));
  }

  // --- Sink: прокидывает обновления в репозиторий/SSE ---
  private final class Sink implements JobWork.ProgressSink {
    private final String id;

    Sink(String id) {
      this.id = id;
    }

    @Override
    public void update(int progress, String message) {
      JobService.this.update(id, progress, message);
    }

    @Override
    public boolean isCanceled() {
      return repo.get(id).getCanceled().get();
    }
  }
}
