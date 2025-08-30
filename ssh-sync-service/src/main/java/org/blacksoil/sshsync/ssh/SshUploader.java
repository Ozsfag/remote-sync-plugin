package org.blacksoil.sshsync.ssh;

import com.intellij.util.Consumer;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.blacksoil.sshsync.ssh.service.SyncOrchestrator;
import org.blacksoil.sshsync.infrastructure.ssh.factory.DefaultSshClientFactory;

@UtilityClass
public class SshUploader {
  private final SyncOrchestrator orchestrator = new SyncOrchestrator(new DefaultSshClientFactory());

  public void uploadFiles(
      List<String> files,
      String localRoot,
      String remotePath,
      String host,
      String username,
      String password,
      Consumer<String> progress)
      throws Exception {
    orchestrator.uploadFiles(files, localRoot, remotePath, host, username, password, progress);
  }

  public void deleteFiles(
      List<String> files,
      String remotePath,
      String host,
      String username,
      String password,
      Consumer<String> progress)
      throws Exception {
    orchestrator.deleteFiles(files, remotePath, host, username, password, progress);
  }

  public void testConnection(String host, String username, String password, String remotePath)
      throws Exception {
    orchestrator.testConnection(host, username, password, remotePath);
  }
}
