package org.blacksoil.sshsync.web.sse;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

class SseStreamExecutorTest {

  @Test
  void sync_path_returns_202_ok() {
    var exec = new SseStreamExecutor(new SimpleAsyncTaskExecutor());
    ResponseEntity<?> resp = exec.run(false, "upload", progress -> {});

    assertEquals(HttpStatus.ACCEPTED, resp.getStatusCode());
    assertTrue(resp.getStatusCode().is2xxSuccessful());
  }

  @Test
  void stream_path_returns_sse_headers_and_executes() throws Exception {
    var exec = new SseStreamExecutor(new SimpleAsyncTaskExecutor());

    CountDownLatch done = new CountDownLatch(1);
    AtomicBoolean ran = new AtomicBoolean(false);

    ResponseEntity<?> resp =
        exec.run(
            true,
            "upload",
            progress -> {
              ran.set(true);
              done.countDown();
            });

    assertEquals(HttpStatus.OK, resp.getStatusCode());
    assertEquals(MediaType.TEXT_EVENT_STREAM, resp.getHeaders().getContentType());
    assertTrue(done.await(2, TimeUnit.SECONDS), "operation body should be executed");
    assertTrue(ran.get());
  }
}
