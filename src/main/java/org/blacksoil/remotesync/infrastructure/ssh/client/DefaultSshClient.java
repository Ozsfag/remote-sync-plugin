package org.blacksoil.remotesync.infrastructure.ssh.client;

import com.jcraft.jsch.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class DefaultSshClient implements SshClient {
  private static final int SSH_PORT = 22;
  private static final int TIMEOUT_MS = 15_000;
  private static final int BUFFER_SIZE = 16 * 1024;

  private final Session session;

  /** Парольная аутентификация. */
  public DefaultSshClient(String host, String username, String password) throws Exception {
    this(host, username, password, null);
  }

  /** Если password == null и есть privateKeyPath — используем ключ. */
  public DefaultSshClient(String host, String username, String password, String privateKeyPath)
      throws Exception {
    JSch jsch = new JSch();
    if (privateKeyPath != null && !privateKeyPath.isBlank()) {
      jsch.addIdentity(privateKeyPath);
    }
    session = jsch.getSession(username, host, SSH_PORT);
    if (password != null && !password.isBlank()) {
      session.setPassword(password);
    }
    Properties config = new Properties();
    // В проде лучше StrictHostKeyChecking=yes и known_hosts
    config.put("StrictHostKeyChecking", "no");
    session.setConfig(config);
    session.connect(TIMEOUT_MS);
  }

  @Override
  public void uploadFile(File localFile, String remoteFilePath) throws Exception {
    String target = normalizeRemotePath(remoteFilePath);
    String remoteDir = parentDir(target);
    execMkdirs(remoteDir);

    // Открываем exec канал под scp -t
    String cmd = "scp -t " + escape(target);
    ChannelExec channel = (ChannelExec) session.openChannel("exec");
    channel.setCommand(cmd);

    try (OutputStream out = channel.getOutputStream();
        InputStream in = channel.getInputStream();
        FileInputStream fis = new FileInputStream(localFile)) {

      channel.connect(TIMEOUT_MS);

      // SCP handshake: ждём ACK от scp -t (0 - OK)
      checkAck(in);

      // отправляем header: C<mode> <size> <filename>\n
      String header = "C0644 " + localFile.length() + " " + localFile.getName() + "\n";
      out.write(header.getBytes(StandardCharsets.UTF_8));
      out.flush();
      checkAck(in);

      // содержимое файла
      byte[] buf = new byte[BUFFER_SIZE];
      int len;
      while ((len = fis.read(buf)) != -1) {
        out.write(buf, 0, len);
      }
      // zero byte to finish
      out.write(0);
      out.flush();
      checkAck(in);
    } finally {
      // дождёмся exit-status (не всегда сразу)
      waitForExit(channel);
      channel.disconnect();
    }
  }

  @Override
  public void deleteFile(String remoteFilePath) throws Exception {
    String target = normalizeRemotePath(remoteFilePath);
    String cmd = "rm -f " + escape(target);

    ChannelExec channel = (ChannelExec) session.openChannel("exec");
    channel.setCommand(cmd);
    ByteArrayOutputStream err = new ByteArrayOutputStream();
    channel.setErrStream(err);

    try {
      channel.connect(TIMEOUT_MS);
      waitForExit(channel);
      int code = channel.getExitStatus();
      if (code != 0) {
        throw new IOException(
            "rm failed with code " + code + ": " + err.toString(StandardCharsets.UTF_8));
      }
    } finally {
      channel.disconnect();
    }
  }

  private void execMkdirs(String dir) throws Exception {
    if (dir == null || dir.isBlank() || "/".equals(dir)) return;
    String cmd = "mkdir -p " + escape(dir);
    ChannelExec channel = (ChannelExec) session.openChannel("exec");
    channel.setCommand(cmd);
    ByteArrayOutputStream err = new ByteArrayOutputStream();
    channel.setErrStream(err);
    try {
      channel.connect(TIMEOUT_MS);
      waitForExit(channel);
      int code = channel.getExitStatus();
      if (code != 0) {
        throw new IOException(
            "mkdir failed with code " + code + ": " + err.toString(StandardCharsets.UTF_8));
      }
    } finally {
      channel.disconnect();
    }
  }

  private static void checkAck(InputStream in) throws IOException {
    int b = in.read();
    if (b == 0) return; // OK
    if (b == -1) throw new EOFException("SCP ack: EOF");
    if (b == 1 || b == 2) { // 1: error, 2: fatal error
      StringBuilder sb = new StringBuilder();
      int c;
      while ((c = in.read()) != '\n' && c != -1) sb.append((char) c);
      throw new IOException("SCP error: " + sb);
    }
    throw new IOException("SCP unexpected ack: " + b);
  }

  private static void waitForExit(ChannelExec ch) throws InterruptedException {
    // Подождём до секунды для заполнения exitStatus
    for (int i = 0; i < 100 && ch.getExitStatus() == -1; i++) {
      Thread.sleep(10);
    }
  }

  private static String normalizeRemotePath(String p) {
    String s = p.replace('\\', '/').trim();
    // уберём повторные // (кроме ведущего слэша)
    return s.replaceAll("(?<!:)/{2,}", "/");
  }

  private static String parentDir(String path) {
    int i = path.lastIndexOf('/');
    return i <= 0 ? "/" : path.substring(0, i);
  }

  private static String escape(String path) {
    // экранирование пробелов и базовых спецсимволов для sh
    String p = path.replace("\\", "\\\\").replace("\"", "\\\"");
    return "\"" + p + "\"";
  }

  @Override
  public void close() {
    if (session != null && session.isConnected()) {
      try {
        session.disconnect();
      } catch (Throwable ignored) {
      }
    }
  }
}
