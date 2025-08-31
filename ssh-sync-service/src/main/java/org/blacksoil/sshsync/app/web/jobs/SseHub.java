package org.blacksoil.sshsync.app.web.jobs;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.RequiredArgsConstructor;
import org.blacksoil.sshsync.app.web.jobs.config.JobProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
@RequiredArgsConstructor
public class SseHub {

  private final JobProperties props;

  private final ConcurrentHashMap<String, CopyOnWriteArrayList<SseEmitter>> emitters =
      new ConcurrentHashMap<>();

  public SseEmitter subscribe(String jobId, Object snapshotPayload) {
    var list = emitters.computeIfAbsent(jobId, k -> new CopyOnWriteArrayList<>());
    var emitter = new SseEmitter(props.getSseTimeoutMs());
    emitter.onTimeout(() -> remove(jobId, emitter));
    emitter.onCompletion(() -> remove(jobId, emitter));
    emitter.onError(e -> remove(jobId, emitter));
    list.add(emitter);
    safeSend(emitter, "snapshot", snapshotPayload);
    return emitter;
  }

  public void broadcast(String jobId, String event, Object payload) {
    List<SseEmitter> list = emitters.get(jobId);
    if (list == null) return;
    for (var em : list) safeSend(em, event, payload);
  }

  public void complete(String jobId) {
    var list = emitters.remove(jobId);
    if (list == null) return;
    for (var em : list) {
      try {
        em.complete();
      } catch (Throwable ignored) {
      }
    }
  }

  private void remove(String jobId, SseEmitter em) {
    var list = emitters.get(jobId);
    if (list != null) list.remove(em);
  }

  private void safeSend(SseEmitter em, String event, Object payload) {
    try {
      em.send(SseEmitter.event().name(event).data(payload));
    } catch (Throwable ignored) {
    }
  }
}
