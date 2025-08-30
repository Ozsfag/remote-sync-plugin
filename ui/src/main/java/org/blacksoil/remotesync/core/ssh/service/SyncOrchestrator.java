package org.blacksoil.remotesync.core.ssh.service;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.Consumer;
import java.io.File;
import java.util.List;
import org.blacksoil.remotesync.core.ssh.ops.RemoteFileOps;
import org.blacksoil.remotesync.core.ssh.path.RemotePathResolver;
import org.blacksoil.remotesync.infrastructure.ssh.client.SshClient;
import org.blacksoil.remotesync.infrastructure.ssh.factory.SshClientFactory;

public record SyncOrchestrator(SshClientFactory clientFactory) {
  private static final Logger LOG = Logger.getInstance(SyncOrchestrator.class);

  public void testConnection(String host, String user, String pass, String remotePath)
      throws Exception {
    String normalized = RemotePathResolver.normalize(remotePath, user);
    LOG.info("=== Testing SSH connection to " + user + "@" + host + " ===");
    LOG.info("Target remote path for verification: " + normalized);
    try (SshClient c = clientFactory.create(host, user, pass)) {
      LOG.info("✔ SSH connection established successfully.");
      RemoteFileOps ops = new RemoteFileOps(c);
      if (!ops.dirExists(normalized)) {
        String msg = "Remote path does not exist: " + normalized;
        LOG.warn(msg);
        throw new IllegalStateException(msg);
      }
      LOG.info("✔ Remote path exists and is accessible.");
    } catch (Exception e) {
      LOG.error("❌ SSH testConnection failed", e);
      throw e;
    }
  }

  public void uploadFiles(
      List<String> files,
      String localRoot,
      String remotePath,
      String host,
      String user,
      String pass,
      Consumer<String> progress)
      throws Exception {
    LOG.info("=== Uploading files via SSH ===");
    LOG.info("Host: " + host + ", Username: " + user);
    LOG.info("Local root: " + localRoot);
    LOG.info("Remote path (raw): " + remotePath);
    LOG.info("Files to upload: " + files);
    if (files == null || files.isEmpty()) {
      LOG.warn("No files to upload");
      return;
    }

    String base = RemotePathResolver.normalize(remotePath, user);
    LOG.info("Normalized remote path: " + base + " (upload)");

    try (SshClient c = clientFactory.create(host, user, pass)) {
      RemoteFileOps ops = new RemoteFileOps(c);
      for (String rel : files) {
        File local = new File(localRoot, rel);
        String target = base + "/" + rel;
        LOG.info("Processing file: " + rel);
        LOG.info("Resolved local file: " + local.getAbsolutePath());
        LOG.info("Resolved target: " + target);
        if (!local.exists()) {
          LOG.warn("Skipping missing file: " + local.getAbsolutePath());
          continue;
        }
        if (progress != null) progress.accept("Uploading: " + rel);
        ops.upload(local, target);
        if (progress != null) progress.accept("Uploaded: " + rel);
      }
    } catch (Exception e) {
      LOG.error("❌ SSH upload failed", e);
      throw e;
    }
  }

  public void deleteFiles(
      List<String> files,
      String remotePath,
      String host,
      String user,
      String pass,
      Consumer<String> progress)
      throws Exception {
    LOG.info("=== Deleting files via SSH ===");
    LOG.info("Files to delete: " + files);
    if (files == null || files.isEmpty()) {
      LOG.warn("No files to delete");
      return;
    }

    String base = RemotePathResolver.normalize(remotePath, user);
    LOG.info("Normalized remote path: " + base + " delete");

    try (SshClient c = clientFactory.create(host, user, pass)) {
      RemoteFileOps ops = new RemoteFileOps(c);
      for (String rel : files) {
        String target = base + "/" + rel;
        if (progress != null) progress.accept("Deleting: " + rel);
        ops.delete(target);
        if (progress != null) progress.accept("Deleted: " + rel);
      }
    } catch (Exception e) {
      LOG.error("❌ SSH delete failed", e);
      throw e;
    }
  }
}
