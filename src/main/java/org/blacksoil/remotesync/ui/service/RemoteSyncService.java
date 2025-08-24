package org.blacksoil.remotesync.ui.service;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.blacksoil.remotesync.backend.diff.GitDiffDetector;
import org.blacksoil.remotesync.backend.ssh.SshUploader;
import org.blacksoil.remotesync.common.DiffResult;
import org.blacksoil.remotesync.ui.secret.Secrets;
import org.blacksoil.remotesync.ui.settings.RemoteSyncSettings;

@UtilityClass
public class RemoteSyncService {
  public interface SyncCallback {
    void onStatus(String message);

    void onError(String error);

    void onComplete();
  }

  private final Logger LOG = Logger.getInstance(RemoteSyncService.class);

  public void sync(Project project, RemoteSyncSettings.State state, SyncCallback callback) {
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
            changed, projectPath, state.remotePath, state.ip, state.username, password);
      }

      if (!deleted.isEmpty()) {
        callback.onStatus("Deleting " + deleted.size() + " file(s)...");
        SshUploader.deleteFiles(deleted, state.remotePath, state.ip, state.username, password);
      }

      callback.onStatus("Sync complete.");
      callback.onComplete();

    } catch (Exception e) {
      LOG.error("Remote sync failed", e);
      callback.onError("Sync failed: " + e.getMessage());
    }
  }

  public void testConnection(Project project, RemoteSyncSettings.State state, SyncCallback cb) {
    try {
      String password = Secrets.loadPassword(project, state.ip, state.username);
      cb.onStatus("Connecting to " + state.ip + "...");
      SshUploader.testConnection(state.ip, state.username, password);
      cb.onComplete();
    } catch (Exception e) {
      cb.onError(e.getMessage());
    }
  }
}
