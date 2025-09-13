package org.blacksoil.remotesync.ui.toolbar.app.command;

import com.intellij.openapi.project.Project;
import java.util.concurrent.atomic.AtomicBoolean;
import org.blacksoil.remotesync.ui.toolbar.app.TaskCallback;
import org.blacksoil.remotesync.ui.toolbar.app.service.RemoteSyncService;
import org.blacksoil.remotesync.ui.toolbar.persistence.RemoteSyncPanelPersistence;
import org.blacksoil.ui.toolbar.view.status.StatusReporter;
import org.blacksoil.ui.toolbar.view.validation.FieldsValidator;
import org.blacksoil.ui.toolbar.view.view.RemoteSyncView;
import org.blacksoil.remotesync.ui.toolbar.settings.RemoteSyncSettings;

public final class TestConnectionCommand implements RemoteCommand {

  @Override
  public String title() {
    return "Testing SSH connection...";
  }

  @Override
  public boolean prepare(
      Project project,
      RemoteSyncSettings settings,
      FieldsValidator validator,
      RemoteSyncView view,
      AtomicBoolean running,
      RemoteSyncPanelPersistence persistence,
      StatusReporter status) {
    if (validator.validate()) {
      status.error("Please fill in all required fields.");
      return false;
    }
    persistence.applyPendingAndPersist(view.collectData(), status::info);
    return true;
  }

  @Override
  public void execute(Project project, RemoteSyncSettings.State state, TaskCallback callback) {
    RemoteSyncService.testConnection(project, state, callback);
  }

  @Override
  public TaskCallback uiCallback(StatusReporter status) {
    return new TaskCallback() {
      public void onStatus(String message) {
        status.info(message);
      }

      public void onError(String error) {
        if (error != null && error.contains("Remote path does not exist")) {
          status.error("Remote path does not exist on server.");
        } else {
          status.error("Connection failed: " + error);
        }
      }

      public void onComplete() {
        status.ok("Connection successful");
      }
    };
  }

  @Override
  public void finish(RemoteSyncView view, AtomicBoolean running) {
    view.setBusy(false);
    running.set(false);
  }
}
