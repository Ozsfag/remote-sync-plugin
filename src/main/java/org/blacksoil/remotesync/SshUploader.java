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
      String privateKeyPath) {
    JSch jsch = new JSch();
    Session session = null;

    try {
      jsch.addIdentity(privateKeyPath);
      session = jsch.getSession(username, host, 22);

      Properties config = new Properties();
      config.put("StrictHostKeyChecking", "no");
      session.setConfig(config);

      session.connect();

      for (String filePath : files) {
        File file = new File(localRoot, filePath);
        String remoteFilePath = remotePath + "/" + filePath;

        uploadFile(session, file, remoteFilePath);
        System.out.println("Uploaded: " + file.getPath() + " → " + remoteFilePath);
      }

    } catch (Exception e) {
      LOG.error("Upload failed.", e);
    } finally {
      if (session != null && session.isConnected()) {
        session.disconnect();
      }
    }
  }

  private static void uploadFile(Session session, File localFile, String remoteFilePath)
      throws Exception {
    String command = "scp -t \"" + remoteFilePath + "\"";
    Channel channel = session.openChannel("exec");
    ((ChannelExec) channel).setCommand(command);

    OutputStream out = channel.getOutputStream();
    FileInputStream fis = new FileInputStream(localFile);

    channel.connect();

    String commandMeta = "C0644 " + localFile.length() + " " + localFile.getName() + "\n";
    out.write(commandMeta.getBytes());
    out.flush();

    byte[] buffer = new byte[1024];
    int len;
    while ((len = fis.read(buffer)) != -1) {
      out.write(buffer, 0, len);
    }
    fis.close();

    out.write(0);
    out.flush();
    out.close();

    channel.disconnect();
  }

  public static void deleteFiles(
      List<String> files, String remotePath, String host, String username, String privateKeyPath) {
    JSch jsch = new JSch();
    Session session = null;

    try {
      jsch.addIdentity(privateKeyPath);
      session = jsch.getSession(username, host, 22);

      Properties config = new Properties();
      config.put("StrictHostKeyChecking", "no");
      session.setConfig(config);

      session.connect();

      for (String filePath : files) {
        String remoteFilePath = remotePath + "/" + filePath;

        String command = "rm -f \"" + remoteFilePath + "\"";
        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);
        channel.connect();
        channel.disconnect();

        System.out.println("Deleted: " + remoteFilePath);
      }

    } catch (Exception e) {
      LOG.error("Deletion failed.", e);
    } finally {
      if (session != null && session.isConnected()) {
        session.disconnect();
      }
    }
  }
}
