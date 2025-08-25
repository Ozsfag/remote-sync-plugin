package org.blacksoil.remotesync.core.ssh;

import com.intellij.openapi.diagnostic.Logger;
import java.io.File;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.blacksoil.remotesync.infrastructure.ssh.client.DefaultSshClient;
import org.blacksoil.remotesync.infrastructure.ssh.client.SshClient;

@UtilityClass
public class SshUploader {
  private final Logger LOG = Logger.getInstance(SshUploader.class);

  public void uploadFiles(
      List<String> files,
      String localRoot,
      String remotePath,
      String host,
      String username,
      String password)
      throws Exception {
    try (SshClient client = new DefaultSshClient(host, username, password)) {
      for (String path : files) {
        File localFile = new File(localRoot, path);
        if (!localFile.exists()) {
          LOG.warn("Skipping missing file: " + localFile.getAbsolutePath());
          continue;
        }
        client.uploadFile(localFile, remotePath + "/" + path);
      }
    }
  }

  public void deleteFiles(
      List<String> files, String remotePath, String host, String username, String password)
      throws Exception {
    try (SshClient client = new DefaultSshClient(host, username, password)) {
      for (String path : files) {
        client.deleteFile(remotePath + "/" + path);
      }
    }
  }

  public void testConnection(String host, String username, String password) throws Exception {
    try (SshClient ignored = new DefaultSshClient(host, username, password)) {
      LOG.info("Connection OK");
    }
  }
}
