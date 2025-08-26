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
    LOG.info("=== Uploading files via SSH ===");
    LOG.info("Host: " + host + ", Username: " + username);
    LOG.info("Local root: " + localRoot);
    LOG.info("Remote path (raw): " + remotePath);
    LOG.info("Files to upload: " + files);

    if (files == null || files.isEmpty()) {
      LOG.warn("No files to upload");
      return;
    }

    String sanitizedRemotePath = normalizeRemotePath(remotePath, username);
    LOG.info("Normalized remote path: " + sanitizedRemotePath + " (upload)");

    LOG.info("Connecting to SSH for upload: " + username + "@" + host);
    try (SshClient client = new DefaultSshClient(host, username, password)) {
      for (String path : files) {
        File localFile = new File(localRoot, path);
        String target = sanitizedRemotePath + "/" + path;

        LOG.info("Processing file: " + path);
        LOG.info("Resolved local file: " + localFile.getAbsolutePath());
        LOG.info("Resolved target: " + target);

        if (!localFile.exists()) {
          LOG.warn("Skipping missing file: " + localFile.getAbsolutePath());
          continue;
        }

        LOG.info("→ Uploading file: " + localFile.getAbsolutePath() + " → " + target);
        client.uploadFile(localFile, target);
        LOG.info("✓ Upload completed: " + target);
      }
    } catch (Exception e) {
      LOG.error("❌ SSH upload failed", e);
      throw e;
    }
  }

  public void deleteFiles(
      List<String> files, String remotePath, String host, String username, String password)
      throws Exception {
    LOG.info("=== Deleting files via SSH ===");
    LOG.info("Files to delete: " + files);

    if (files == null || files.isEmpty()) {
      LOG.warn("No files to delete");
      return;
    }

    String sanitizedRemotePath = normalizeRemotePath(remotePath, username);
    LOG.info("Normalized remote path: " + sanitizedRemotePath + (" delete"));

    LOG.info("Connecting to SSH for deletion: " + username + "@" + host);
    try (SshClient client = new DefaultSshClient(host, username, password)) {
      for (String path : files) {
        String target = sanitizedRemotePath + "/" + path;
        LOG.info("→ Deleting file: " + target);
        client.deleteFile(target);
        LOG.info("✓ Deleted file: " + target);
      }
    } catch (Exception e) {
      LOG.error("❌ SSH delete failed", e);
      throw e;
    }
  }

  public void testConnection(String host, String username, String password, String remotePath)
      throws Exception {
    String normalizedPath = normalizeRemotePath(remotePath, username);

    LOG.info("=== Testing SSH connection to " + username + "@" + host + " ===");
    LOG.info("Target remote path for verification: " + normalizedPath);

    try (SshClient client = new DefaultSshClient(host, username, password)) {
      LOG.info("✔ SSH connection established successfully.");

      LOG.info("→ Verifying remote path existence...");
      boolean exists = client.directoryExists(normalizedPath);
      LOG.info("Path check result: " + (exists ? "EXISTS ✅" : "MISSING ❌"));

      if (!exists) {
        String msg = "Remote path does not exist: " + normalizedPath;
        LOG.warn(msg);
        throw new IllegalStateException(msg);
      }

      LOG.info("✔ Remote path exists and is accessible.");
    } catch (Exception e) {
      LOG.error("❌ SSH testConnection failed", e);
      throw e;
    }
  }

  private String normalizeRemotePath(String path, String username) {
    if (path == null || path.isBlank()) {
      return "/home/" + username + "/remote-sync";
    }

    path = path.trim();

    // Удаляем любые кавычки по краям
    while ((path.startsWith("\"") && path.endsWith("\""))
        || (path.startsWith("'") && path.endsWith("'"))) {
      path = path.substring(1, path.length() - 1).trim();
    }

    // Удаляем .git
    if (path.endsWith(".git")) {
      path = path.substring(0, path.length() - 4);
    }

    // Если это Git URL — достаём имя репозитория
    if ((path.startsWith("http") || path.contains("@")) && path.contains("/")) {
      path = path.substring(path.lastIndexOf('/') + 1);
    }

    // Подставляем /home/username если указано ~/
    if (path.startsWith("~/")) {
      path = "/home/" + username + path.substring(1); // заменяем ~ → /home/username
    }

    // Если просто имя (например, "project")
    if (!path.startsWith("/")) {
      path = "/home/" + username + "/" + path;
    }

    return path;
  }
}
