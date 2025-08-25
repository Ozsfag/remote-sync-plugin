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
    if (files == null || files.isEmpty()) {
      LOG.warn("No files to upload");
      return;
    }

    LOG.info("Connecting to SSH for upload: " + username + "@" + host);
    try (SshClient client = new DefaultSshClient(host, username, password)) {
      for (String path : files) {
        File localFile = new File(localRoot, path);
        String target = remotePath + "/" + path;

        if (!localFile.exists()) {
          LOG.warn("Skipping missing file: " + localFile.getAbsolutePath());
          continue;
        }

        LOG.info("Uploading file: " + localFile.getAbsolutePath() + " → " + target);
        client.uploadFile(localFile, target);
        LOG.info("Upload completed: " + target);
      }
    } catch (Exception e) {
      LOG.error("SSH upload failed", e);
      throw e;
    }
  }

  public void deleteFiles(
      List<String> files, String remotePath, String host, String username, String password)
      throws Exception {
    if (files == null || files.isEmpty()) {
      LOG.warn("No files to delete");
      return;
    }

    LOG.info("Connecting to SSH for deletion: " + username + "@" + host);
    try (SshClient client = new DefaultSshClient(host, username, password)) {
      for (String path : files) {
        String target = remotePath + "/" + path;
        LOG.info("Deleting file: " + target);
        client.deleteFile(target);
        LOG.info("Deleted file: " + target);
      }
    } catch (Exception e) {
      LOG.error("SSH delete failed", e);
      throw e;
    }
  }

  public void testConnection(String host, String username, String password) throws Exception {
    LOG.info("Testing SSH connection: " + username + "@" + host);
    try (SshClient ignored = new DefaultSshClient(host, username, password)) {
      LOG.info("Connection OK");
    } catch (Exception e) {
      LOG.error("Connection failed", e);
      throw e;
    }
  }
}
