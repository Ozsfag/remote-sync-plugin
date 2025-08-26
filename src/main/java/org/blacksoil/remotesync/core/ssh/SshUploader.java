package org.blacksoil.remotesync.core.ssh;

import java.util.List;
import lombok.experimental.UtilityClass;
import org.blacksoil.remotesync.infrastructure.ssh.factory.DefaultSshClientFactory;
import org.blacksoil.remotesync.core.ssh.service.SyncOrchestrator;

@UtilityClass
public class SshUploader {
  private final SyncOrchestrator orchestrator = new SyncOrchestrator(new DefaultSshClientFactory());

  public void uploadFiles(
      List<String> files,
      String localRoot,
      String remotePath,
      String host,
      String username,
      String password)
      throws Exception {
    orchestrator.uploadFiles(files, localRoot, remotePath, host, username, password);
  }

  public void deleteFiles(
      List<String> files, String remotePath, String host, String username, String password)
      throws Exception {
    orchestrator.deleteFiles(files, remotePath, host, username, password);
  }

  public void testConnection(String host, String username, String password, String remotePath)
      throws Exception {
    orchestrator.testConnection(host, username, password, remotePath);
  }
}
