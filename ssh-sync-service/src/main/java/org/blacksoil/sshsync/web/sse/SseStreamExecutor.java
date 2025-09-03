package org.blacksoil.sshsync.web.sse;

import java.util.Map;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
@RequiredArgsConstructor
@Slf4j
public class SseStreamExecutor {

  private final AsyncTaskExecutor taskExecutor;

  public ResponseEntity<?> run(boolean stream, String opName, ProgressOp body) {
    if (stream) {
      SseEmitter emitter = new SseEmitter(0L);
      emitter.onCompletion(() -> log.debug("SSE {} completed", opName));
      emitter.onTimeout(() -> log.warn("SSE {} timeout", opName));
      emitter.onError(ex -> log.error("SSE {} error", opName, ex));

      taskExecutor.submit(
          () -> {
            try {
              SseSupport.send(emitter, "status", Map.of("state", "STARTED"));
              Consumer<String> progress =
                  msg -> SseSupport.sendSafe(emitter, "progress", Map.of("message", msg));
              body.run(progress);
              SseSupport.send(emitter, "status", Map.of("state", "DONE"));
              emitter.complete();
            } catch (Throwable t) {
              log.error("{} failed", opName, t);
              SseSupport.sendSafe(emitter, "error", Map.of("message", t.getMessage()));
              emitter.completeWithError(t);
            }
          });

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.TEXT_EVENT_STREAM);
      headers.setCacheControl(CacheControl.noStore().getHeaderValue());
      headers.add(HttpHeaders.CONNECTION, "keep-alive");
      headers.add("X-Content-Type-Options", "nosniff"); // <-- фикс

      return new ResponseEntity<>(emitter, headers, HttpStatus.OK);
    }

    try {
      body.run(s -> {});
      return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("ok", true));
    } catch (Exception e) {
      log.error("{} failed (sync)", opName, e);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
    }
  }

  @FunctionalInterface
  public interface ProgressOp {
    void run(Consumer<String> progress) throws Exception;
  }
}
