package org.blacksoil.remotesync.ui.pluginbar.app;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import lombok.experimental.UtilityClass;
import org.blacksoil.remotesync.ui.pluginbar.app.command.RemoteCommand;
import org.blacksoil.remotesync.ui.pluginbar.settings.RemoteSyncSettings;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public final class BackgroundTaskRunnerWrapper {

  public void run(
      Project project,
      RemoteCommand task,
      RemoteSyncSettings.State state,
      TaskCallback uiCallback,
      Runnable onFinished) {

    new Task.Backgroundable(project, task.title(), true) {
      @Override
      public void run(@NotNull ProgressIndicator indicator) {
        TaskCallback wrapped = new IndicatorTaskCallback(indicator, uiCallback);
        task.execute(project, state, wrapped);
      }

      @Override
      public void onFinished() {
        if (onFinished != null) onFinished.run();
      }
    }.queue();
  }
}
