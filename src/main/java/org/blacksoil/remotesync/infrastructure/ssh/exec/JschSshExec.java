package org.blacksoil.remotesync.infrastructure.ssh.exec;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public record JschSshExec(Session session) implements SshExec {
  public JschSshExec(Session session) {
    this.session = Objects.requireNonNull(session, "session");
  }

  @Override
  public ExecResult run(String command, int timeoutMs) throws Exception {
    ChannelExec channel = (ChannelExec) session.openChannel("exec");
    channel.setCommand(command);

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ByteArrayOutputStream err = new ByteArrayOutputStream();
    channel.setOutputStream(out);
    channel.setErrStream(err);

    try {
      channel.connect(timeoutMs);
      waitForExit(channel);
      int code = channel.getExitStatus();
      return new ExecResult(
          code, out.toString(StandardCharsets.UTF_8), err.toString(StandardCharsets.UTF_8));
    } finally {
      channel.disconnect();
    }
  }

  private static void waitForExit(ChannelExec ch) throws InterruptedException {
    for (int i = 0; i < 100 && ch.getExitStatus() == -1; i++) {
      Thread.sleep(10);
    }
  }
}
