package org.blacksoil.remotesync.ui.toolbar.app.service;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.blacksoil.remotesync.ui.toolbar.app.TaskCallback;
import org.blacksoil.remotesync.ui.toolbar.app.client.GitDiffClient;
import org.blacksoil.remotesync.ui.toolbar.app.client.SshSyncClient;
import org.blacksoil.remotesync.ui.toolbar.app.validator.SyncValidator;
import org.blacksoil.remotesync.ui.toolbar.secret.Secrets;
import org.blacksoil.remotesync.ui.toolbar.settings.RemoteSyncSettings;
import org.blacksoil.shareddto.gitdiff.GitDiffRequest;
import org.blacksoil.shareddto.gitdiff.GitDiffResponse;
import org.blacksoil.shareddto.sshsync.DeleteRequest;
import org.blacksoil.shareddto.sshsync.TestRequest;
import org.blacksoil.shareddto.sshsync.UploadRequest;

@UtilityClass
public class RemoteSyncService {
  private final Logger LOG = Logger.getInstance(RemoteSyncService.class);

  private final String GIT_DIFF_BASE_URL =
      System.getProperty(
          "REMOTESYNC_GIT_DIFF_BASE_URL",
          System.getenv().getOrDefault("REMOTESYNC_GIT_DIFF_BASE_URL", "http://127.0.0.1:8081"));

  private final String SSH_SYNC_BASE_URL =
      System.getProperty(
          "REMOTESYNC_SSH_SYNC_BASE_URL",
          System.getenv().getOrDefault("REMOTESYNC_SSH_SYNC_BASE_URL", "http://127.0.0.1:8082"));

  public void sync(Project project, RemoteSyncSettings.State state, TaskCallback callback) {
    String projectBasePath = project.getBasePath();
    if (SyncValidator.isNotValid(project, state, projectBasePath)) {
      isNotValidParameterAction(callback);
      return;
    }

    String ip = state.ip;
    String username = state.username;
    String password = Secrets.loadPassword(project, ip, username);

    try {
      callback.onStatus("Detecting changes...");

      GitDiffClient gitDiffClient = new GitDiffClient(GIT_DIFF_BASE_URL);
      GitDiffResponse result =
          gitDiffClient.getDiff(new GitDiffRequest(projectBasePath, state.branch));

      List<String> changed = result.addedOrModified();
      List<String> deleted = result.deleted();

      if (SyncValidator.isEmpty(changed, deleted)) {
        callback.onStatus("No changes.");
        callback.onComplete();
        return;
      }

      SshSyncClient sshClient = new SshSyncClient(SSH_SYNC_BASE_URL);

      if (!SyncValidator.isEmpty(changed)) {
        callback.onStatus("Uploading " + changed.size() + " file(s)...");
        sshClient.uploadFiles(
            new UploadRequest(changed, ip, username, password, state.remotePath, projectBasePath));
      }

      if (!SyncValidator.isEmpty(deleted)) {
        callback.onStatus("Deleting " + deleted.size() + " file(s)...");
        sshClient.deleteFiles(new DeleteRequest(deleted, ip, username, password, state.remotePath));
      }

      callback.onStatus("Sync complete.");
      callback.onComplete();
    } catch (Exception e) {
      LOG.error("Remote sync failed", e);
      callback.onError("Sync failed: " + e.getMessage());
    }
  }

  private void isNotValidParameterAction(TaskCallback callback) {
    LOG.warn("Invalid sync parameters.");
    callback.onError("Invalid project or configuration.");
  }

  public void testConnection(Project project, RemoteSyncSettings.State state, TaskCallback cb) {
    try {
      String password = Secrets.loadPassword(project, state.ip, state.username);
      cb.onStatus("Connecting to " + state.ip + "...");

      SshSyncClient sshClient = new SshSyncClient(SSH_SYNC_BASE_URL);
      sshClient.testConnection(
          new TestRequest(state.ip, state.username, password, state.remotePath));

      cb.onStatus("Remote directory exists: " + state.remotePath);
      cb.onComplete();
    } catch (Exception e) {
      LOG.error("Connection test failed", e);
      cb.onError(e.getMessage());
    }
  }
}
