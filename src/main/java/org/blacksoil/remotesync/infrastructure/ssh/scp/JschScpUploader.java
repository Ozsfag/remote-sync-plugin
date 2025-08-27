package org.blacksoil.remotesync.infrastructure.ssh.scp;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.blacksoil.remotesync.infrastructure.ssh.util.ShellEscaper;

public record JschScpUploader(Session session) implements ScpUploader {
  private static final int TIMEOUT_MS = 15_000;
  private static final int BUFFER_SIZE = 16 * 1024;

  public JschScpUploader(Session session) {
    this.session = Objects.requireNonNull(session, "session");
  }

  @Override
  public void upload(File localFile, String remoteDir, String remoteFileName) throws Exception {
    // Открываем exec канал под scp -t
    String cmd = "scp -t " + ShellEscaper.quote(remoteDir);
    ChannelExec channel = (ChannelExec) session.openChannel("exec");
    channel.setCommand(cmd);

    try (OutputStream out = channel.getOutputStream();
        InputStream in = channel.getInputStream();
        FileInputStream fis = new FileInputStream(localFile)) {

      channel.connect(TIMEOUT_MS);

      checkAck(in); // handshake

      // header: C<mode> <size> <filename>\n
      String header = "C0644 " + localFile.length() + " " + remoteFileName + "\n";
      out.write(header.getBytes(StandardCharsets.UTF_8));
      out.flush();
      checkAck(in);

      // отправляем содержимое
      byte[] buf = new byte[BUFFER_SIZE];
      for (int len; (len = fis.read(buf)) != -1; ) {
        out.write(buf, 0, len);
      }

      // zero byte to finish
      out.write(0);
      out.flush();
      checkAck(in);
    } finally {
      waitForExit(channel);
      channel.disconnect();
    }
  }

  private static void waitForExit(ChannelExec ch) throws InterruptedException {
    for (int i = 0; i < 100 && ch.getExitStatus() == -1; i++) {
      Thread.sleep(10);
    }
  }

  private static void checkAck(InputStream in) throws IOException {
    int b = in.read();
    if (b == 0) return; // OK
    if (b == -1) throw new EOFException("SCP ack: EOF");
    if (b == 1 || b == 2) {
      StringBuilder sb = new StringBuilder();
      for (int c; (c = in.read()) != '\n' && c != -1; ) sb.append((char) c);
      throw new IOException("SCP error: " + sb);
    }
    throw new IOException("SCP unexpected ack: " + b);
  }
}
