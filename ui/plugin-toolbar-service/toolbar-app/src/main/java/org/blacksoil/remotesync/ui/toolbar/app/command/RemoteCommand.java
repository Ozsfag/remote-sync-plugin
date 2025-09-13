package org.blacksoil.remotesync.ui.toolbar.app.command;

import com.intellij.openapi.project.Project;
import java.util.concurrent.atomic.AtomicBoolean;
import org.blacksoil.remotesync.ui.toolbar.app.TaskCallback;
import org.blacksoil.remotesync.ui.toolbar.persistence.RemoteSyncPanelPersistence;
import org.blacksoil.ui.toolbar.view.status.StatusReporter;
import org.blacksoil.ui.toolbar.view.validation.FieldsValidator;
import org.blacksoil.ui.toolbar.view.view.RemoteSyncView;
import org.blacksoil.remotesync.ui.toolbar.settings.RemoteSyncSettings;

public interface RemoteCommand {
  String title();

  boolean prepare(
      Project project,
      RemoteSyncSettings settings,
      FieldsValidator validator,
      RemoteSyncView view,
      AtomicBoolean running,
      RemoteSyncPanelPersistence persistence,
      StatusReporter status);

  void execute(Project project, RemoteSyncSettings.State state, TaskCallback callback);

  TaskCallback uiCallback(StatusReporter status);

  void finish(RemoteSyncView view, AtomicBoolean running);
}
