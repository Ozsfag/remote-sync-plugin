package org.blacksoil.sshsync.infra.exec;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class SshExec {
  private static final int DEFAULT_TIMEOUT_MS = 15_000;

  public ExecResult run(Session session, String command) throws Exception {
    return run(session, command, DEFAULT_TIMEOUT_MS);
  }

  public ExecResult run(Session session, String command, int timeoutMs) throws Exception {
    Objects.requireNonNull(session);
    Objects.requireNonNull(command);
    ChannelExec ch = (ChannelExec) session.openChannel("exec");
    ch.setCommand(command);

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ByteArrayOutputStream err = new ByteArrayOutputStream();
    ch.setOutputStream(out);
    ch.setErrStream(err);

    try {
      ch.connect(timeoutMs);
      for (int i = 0; i < 100 && ch.getExitStatus() == -1; i++) Thread.sleep(10);
      return new ExecResult(
          ch.getExitStatus(),
          out.toString(StandardCharsets.UTF_8),
          err.toString(StandardCharsets.UTF_8));
    } finally {
      ch.disconnect();
    }
  }

  public record ExecResult(int exitCode, String stdout, String stderr) {}
}
