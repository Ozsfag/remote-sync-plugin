package org.blacksoil.sshsync.web.sse;

import java.io.IOException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public final class SseSupport {
  private SseSupport() {}

  public static void send(SseEmitter emitter, String event, Object data) throws IOException {
    emitter.send(SseEmitter.event().name(event).data(data));
  }

  public static void sendSafe(SseEmitter emitter, String event, Object data) {
    try {
      send(emitter, event, data);
    } catch (IOException ignored) {
    }
  }
}
