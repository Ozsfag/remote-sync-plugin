package org.blacksoil.remotesync.backend.ssh.client;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.Properties;

public class DefaultSshClient implements SshClient {
  private static final int SSH_PORT = 22;
  private static final int BUFFER_SIZE = 1024;
  private final Session session;

  public DefaultSshClient(String host, String username, String password) throws Exception {
    JSch jsch = new JSch();
    session = jsch.getSession(username, host, SSH_PORT);
    session.setPassword(password);
    Properties config = new Properties();
    config.put("StrictHostKeyChecking", "no");
    session.setConfig(config);
    session.connect();
  }

  @Override
  public void uploadFile(File localFile, String remoteFilePath) throws Exception {
    String command = "scp -t " + escape(remoteFilePath);
    ChannelExec channel = (ChannelExec) session.openChannel("exec");
    channel.setCommand(command);

    try (OutputStream out = channel.getOutputStream();
        FileInputStream fis = new FileInputStream(localFile)) {

      channel.connect();
      String meta = String.format("C0644 %d %s%n", localFile.length(), localFile.getName());
      out.write(meta.getBytes());
      out.flush();

      byte[] buffer = new byte[BUFFER_SIZE];
      int len;
      while ((len = fis.read(buffer)) != -1) {
        out.write(buffer, 0, len);
      }

      out.write(0);
      out.flush();
    } finally {
      channel.disconnect();
    }
  }

  @Override
  public void deleteFile(String remoteFilePath) throws Exception {
    String command = "rm -f " + escape(remoteFilePath);
    ChannelExec channel = (ChannelExec) session.openChannel("exec");
    channel.setCommand(command);
    try {
      channel.connect();
    } finally {
      channel.disconnect();
    }
  }

  private String escape(String path) {
    return path.replace(" ", "\\ ");
  }

  @Override
  public void close() {
    if (session != null && session.isConnected()) {
      session.disconnect();
    }
  }
}
