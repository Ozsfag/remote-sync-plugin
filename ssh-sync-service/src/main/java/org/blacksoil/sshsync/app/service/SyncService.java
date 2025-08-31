package org.blacksoil.sshsync.app.service;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.blacksoil.sshsync.app.client.SshClient;
import org.blacksoil.sshsync.domain.service.SshSessionFactory;
import org.blacksoil.sshsync.domain.util.RemotePathResolver;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyncService {

  private final SshSessionFactory sessionFactory;

  public void testConnection(String host, String user, String pass, String remotePath)
      throws Exception {
    String normalized = RemotePathResolver.normalize(remotePath, user);
    try (SshClient client = sessionFactory.create(host, user, pass)) {
      if (!client.directoryExists(normalized)) {
        throw new IllegalStateException("Remote path does not exist: " + normalized);
      }
      log.info("SSH connection OK, path exists: {}", normalized);
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
    if (files == null || files.isEmpty()) {
      log.warn("No files to upload");
      return;
    }
    String base = RemotePathResolver.normalize(remotePath, user);

    try (SshClient client = sessionFactory.create(host, user, pass)) {
      for (String rel : files) {
        File local = new File(localRoot, rel);
        if (!local.exists()) {
          log.warn("Skip missing: {}", local.getAbsolutePath());
          continue;
        }
        String target = base + "/" + rel;
        if (progress != null) progress.accept("Uploading: " + rel);
        client.uploadFile(local, target);
        if (progress != null) progress.accept("Uploaded: " + rel);
      }
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
    if (files == null || files.isEmpty()) {
      log.warn("No files to delete");
      return;
    }
    String base = RemotePathResolver.normalize(remotePath, user);

    try (SshClient client = sessionFactory.create(host, user, pass)) {
      for (String rel : files) {
        String target = base + "/" + rel;
        if (progress != null) progress.accept("Deleting: " + rel);
        client.deleteFile(target);
        if (progress != null) progress.accept("Deleted: " + rel);
      }
    }
  }
}
