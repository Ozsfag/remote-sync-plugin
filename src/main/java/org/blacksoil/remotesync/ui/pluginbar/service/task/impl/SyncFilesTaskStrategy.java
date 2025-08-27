package org.blacksoil.remotesync.ui.pluginbar.service.task.impl;

import com.intellij.openapi.project.Project;
import java.util.concurrent.atomic.AtomicBoolean;

import org.blacksoil.remotesync.ui.pluginbar.service.RemoteSyncService;
import org.blacksoil.remotesync.ui.pluginbar.service.StatusReporter;
import org.blacksoil.remotesync.ui.pluginbar.components.persistence.RemoteSyncPanelPersistence;
import org.blacksoil.remotesync.ui.pluginbar.service.task.api.RemoteTaskStrategy;
import org.blacksoil.remotesync.ui.pluginbar.service.task.api.TaskCallback;
import org.blacksoil.remotesync.ui.pluginbar.settings.RemoteSyncSettings;
import org.blacksoil.remotesync.ui.pluginbar.view.RemoteSyncView;
import org.blacksoil.remotesync.ui.pluginbar.view.validator.FieldsValidator;

public final class SyncFilesTaskStrategy implements RemoteTaskStrategy {

  @Override
  public String title() {
    return "Running sync...";
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
    if (!running.compareAndSet(false, true)) {
      status.info("Another task is already running…");
      return false;
    }
    view.setBusy(true);
    status.info("Syncing...");

    if (!validator.validate()) {
      status.error("Please fill in all required fields.");
      view.setBusy(false);
      running.set(false);
      return false;
    }

    persistence.applyPendingAndPersist(view.collectData(), status::info);
    return true;
  }

  @Override
  public void execute(Project project, RemoteSyncSettings.State state, TaskCallback callback) {
    RemoteSyncService.sync(project, state, callback);
  }

  @Override
  public TaskCallback uiCallback(StatusReporter status) {
    return new TaskCallback() {
      public void onStatus(String message) {
        status.info(message);
      }

      public void onError(String error) {
        status.error("Sync failed: " + error);
      }

      public void onComplete() {
        status.ok("Sync complete");
      }
    };
  }

  @Override
  public void finish(RemoteSyncView view, AtomicBoolean running) {
    view.setBusy(false);
    running.set(false);
  }
}
