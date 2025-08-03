package org.blacksoil.remotesync;

import com.intellij.openapi.diagnostic.Logger;
import com.jcraft.jsch.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

public class SshUploader {
  private static final Logger LOG = Logger.getInstance(SshUploader.class);

  public static void uploadFiles(
      List<String> files,
      String localRoot,
      String remotePath,
      String host,
      String username,
      String privateKeyPath)
      throws Exception {
    try (SshSession session = new SshSession(host, username, privateKeyPath)) {
      for (String relativePath : files) {
        File localFile = new File(localRoot, relativePath);
        if (!localFile.exists()) {
          LOG.warn("Skipping missing file: " + localFile.getAbsolutePath());
          continue;
        }
        String remoteFile = remotePath + "/" + relativePath;
        session.uploadFile(localFile, remoteFile);
        LOG.info("Uploaded: " + localFile.getPath() + " → " + remoteFile);
      }
    }
  }

  public static void deleteFiles(
      List<String> files, String remotePath, String host, String username, String privateKeyPath)
      throws Exception {
    try (SshSession session = new SshSession(host, username, privateKeyPath)) {
      for (String relativePath : files) {
        String remoteFile = remotePath + "/" + relativePath;
        session.deleteFile(remoteFile);
        LOG.info("Deleted: " + remoteFile);
      }
    }
  }

  static class SshSession implements AutoCloseable {
    private final Session session;

    SshSession(String host, String username, String privateKeyPath) throws Exception {
      JSch jsch = new JSch();
      jsch.addIdentity(privateKeyPath);
      session = jsch.getSession(username, host, 22);

      Properties config = new Properties();
      config.put("StrictHostKeyChecking", "no");
      session.setConfig(config);

      session.connect();
    }

    void uploadFile(File localFile, String remoteFilePath) throws Exception {
      String command = "scp -t " + remoteFilePath;
      ChannelExec channel = (ChannelExec) session.openChannel("exec");
      channel.setCommand(command);

      try (OutputStream out = channel.getOutputStream();
          FileInputStream fis = new FileInputStream(localFile)) {
        channel.connect();

        String meta = "C0644 " + localFile.length() + " " + localFile.getName() + "\n";
        out.write(meta.getBytes());
        out.flush();

        byte[] buffer = new byte[1024];
        int len;
        while ((len = fis.read(buffer)) != -1) {
          out.write(buffer, 0, len);
        }

        out.write(0);
        out.flush();
      }

      channel.disconnect();
    }

    void deleteFile(String remoteFilePath) throws Exception {
      String command = "rm -f " + escape(remoteFilePath);
      ChannelExec channel = (ChannelExec) session.openChannel("exec");
      channel.setCommand(command);
      channel.connect();
      channel.disconnect();
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
}
