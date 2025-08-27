package org.blacksoil.remotesync.ui.pluginbar.service;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.blacksoil.remotesync.core.gitdiff.GitDiffDetector;
import org.blacksoil.remotesync.core.model.DiffResult;
import org.blacksoil.remotesync.core.ssh.SshUploader;
import org.blacksoil.remotesync.core.ssh.path.RemotePathResolver;
import org.blacksoil.remotesync.ui.pluginbar.secret.Secrets;
import org.blacksoil.remotesync.ui.pluginbar.service.task.api.TaskCallback;
import org.blacksoil.remotesync.ui.pluginbar.settings.RemoteSyncSettings;

@UtilityClass
public class RemoteSyncService {
  private final Logger LOG = Logger.getInstance(RemoteSyncService.class);

  public void sync(Project project, RemoteSyncSettings.State state, TaskCallback callback) {
    if (project == null || state == null || project.getBasePath() == null) {
      LOG.warn("Invalid sync parameters.");
      callback.onError("Invalid project or configuration.");
      return;
    }

    String password = Secrets.loadPassword(project, state.ip, state.username);
    try {
      callback.onStatus("Detecting changes...");
      String projectPath = project.getBasePath();

      DiffResult diff = GitDiffDetector.getChangedFiles(projectPath, state.branch);
      List<String> changed = diff.addedOrModified();
      List<String> deleted = diff.deleted();

      if (changed.isEmpty() && deleted.isEmpty()) {
        callback.onStatus("No changes.");
        callback.onComplete();
        return;
      }

      if (!changed.isEmpty()) {
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

      if (!deleted.isEmpty()) {
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
