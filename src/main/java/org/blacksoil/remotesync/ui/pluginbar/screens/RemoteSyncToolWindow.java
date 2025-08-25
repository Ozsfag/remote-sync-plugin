package org.blacksoil.remotesync.ui.pluginbar.screens;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.blacksoil.remotesync.ui.pluginbar.components.RemoteSyncPanel;
import org.jetbrains.annotations.NotNull;

public class RemoteSyncToolWindow implements ToolWindowFactory {
  @Override
  public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
    // контент тул‑виндоу сам является Disposable
    ContentFactory cf = ContentFactory.getInstance();
    RemoteSyncPanel panel = new RemoteSyncPanel(project, toolWindow.getContentManager());

    Content content = cf.createContent(panel.getContent(), "Remote Sync", false);
    // ✅ назначаем disposer — теперь IDE сама вызовет dispose() у панели
    content.setDisposer(panel);

    toolWindow.getContentManager().addContent(content);
  }
}
