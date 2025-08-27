package org.blacksoil.remotesync.ui.pluginbar.components;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JPanel;

import org.blacksoil.remotesync.ui.pluginbar.components.persistence.RemoteSyncPanelPersistence;
import org.blacksoil.remotesync.ui.pluginbar.model.FormData;
import org.blacksoil.remotesync.ui.pluginbar.service.*;
import org.blacksoil.remotesync.ui.pluginbar.service.task.api.RemoteTaskStrategy;
import org.blacksoil.remotesync.ui.pluginbar.service.task.impl.SyncFilesTaskStrategy;
import org.blacksoil.remotesync.ui.pluginbar.service.task.impl.TestConnectionTaskStrategy;
import org.blacksoil.remotesync.ui.pluginbar.settings.RemoteSyncSettings;
import org.blacksoil.remotesync.ui.pluginbar.service.task.util.BackgroundTaskRunnerWrapper;
import org.blacksoil.remotesync.ui.pluginbar.view.RemoteSyncView;
import org.blacksoil.remotesync.ui.pluginbar.view.validator.FieldsValidator;
import org.jetbrains.annotations.NotNull;

public final class RemoteSyncPanel implements Disposable {

  private final Project project;
  private final RemoteSyncSettings settings;

  private final RemoteSyncView view = new RemoteSyncView();
  private final StatusReporter status = new StatusReporter(view);
  private final FieldsValidator validator = new FieldsValidator(view);

  private final AtomicBoolean running = new AtomicBoolean(false);
  private final RemoteSyncPanelPersistence persistence;

  public RemoteSyncPanel(@NotNull Project project, @NotNull Disposable parentDisposable) {
    this.project = project;
    this.settings = RemoteSyncSettings.getInstance(project);
    this.persistence = new RemoteSyncPanelPersistence(project, settings, 400);

    Disposer.register(parentDisposable, this);

    view.setData(FormData.from(project, settings.getState()));
    view.onChange(() -> persistence.schedulePersist(view.collectData()));
    view.onTest(() -> runTask(new TestConnectionTaskStrategy()));
    view.onSync(() -> runTask(new SyncFilesTaskStrategy()));
  }

  public JPanel getContent() {
    return view.getRoot();
  }

  private void runTask(RemoteTaskStrategy task) {
    if (!task.prepare(project, settings, validator, view, running, persistence, status)) {
      return;
    }
    BackgroundTaskRunnerWrapper.run(
        project,
        task,
        settings.getState(),
        task.uiCallback(status),
        () -> task.finish(view, running));
  }

  @Override
  public void dispose() {
    persistence.cancel();
  }
}
