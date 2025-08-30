package org.blacksoil.remotesync.ui.pluginbar.application.service;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.blacksoil.dto.GitDiffRequest;
import org.blacksoil.dto.GitDiffResponse;
import org.blacksoil.remotesync.core.ssh.SshUploader;
import org.blacksoil.remotesync.core.ssh.path.RemotePathResolver;
import org.blacksoil.remotesync.infrastructure.secret.Secrets;
import org.blacksoil.remotesync.ui.pluginbar.application.TaskCallback;
import org.blacksoil.remotesync.ui.pluginbar.application.client.GitDiffClient;
import org.blacksoil.remotesync.ui.pluginbar.application.validator.SyncValidator;
import org.blacksoil.remotesync.ui.pluginbar.settings.RemoteSyncSettings;

@UtilityClass
public class RemoteSyncService {
  private final Logger LOG = Logger.getInstance(RemoteSyncService.class);

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

      GitDiffClient gitDiffClient = new GitDiffClient("http://localhost:8080");
      String projectPath = project.getBasePath();
      String branch = state.branch;

      GitDiffResponse result = gitDiffClient.getDiff(new GitDiffRequest(projectPath, branch));
      List<String> changed = result.addedOrModified();
      List<String> deleted = result.deleted();

      if (SyncValidator.isEmpty(changed, deleted)) {
        callback.onStatus("No changes.");
        callback.onComplete();
        return;
      }

      if (!SyncValidator.isEmpty(changed)) {
        callback.onStatus("Uploading " + changed.size() + " file(s)...");
        SshUploader.uploadFiles(
            changed,
            projectPath,
            state.remotePath,
            state.ip,
            state.username,
            password,
            callback::onStatus);
      }

      if (!SyncValidator.isEmpty(deleted)) {
        callback.onStatus("Deleting " + deleted.size() + " file(s)...");
        SshUploader.deleteFiles(
            deleted, state.remotePath, state.ip, state.username, password, callback::onStatus);
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
      SshUploader.testConnection(state.ip, state.username, password, state.remotePath);
      String normalized = RemotePathResolver.normalize(state.remotePath, state.username);
      cb.onStatus("Remote directory exists: " + normalized);
      cb.onComplete();
    } catch (Exception e) {
      cb.onError(e.getMessage());
    }
  }
}
