package org.blacksoil.sshsync.app.facade;

import java.util.List;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.blacksoil.sshsync.app.service.SyncService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SshUploader {

  private final SyncService syncService;

  public void uploadFiles(
      List<String> files,
      String localRoot,
      String remotePath,
      String host,
      String username,
      String password,
      Consumer<String> progress)
      throws Exception {
    syncService.uploadFiles(files, localRoot, remotePath, host, username, password, progress);
  }

  public void deleteFiles(
      List<String> files,
      String remotePath,
      String host,
      String username,
      String password,
      Consumer<String> progress)
      throws Exception {
    syncService.deleteFiles(files, remotePath, host, username, password, progress);
  }

  public void testConnection(String host, String username, String password, String remotePath)
      throws Exception {
    syncService.testConnection(host, username, password, remotePath);
  }
}
