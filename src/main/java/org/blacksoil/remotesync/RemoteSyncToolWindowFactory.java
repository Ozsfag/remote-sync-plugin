package org.blacksoil.remotesync;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

public class RemoteSyncToolWindowFactory implements ToolWindowFactory, DumbAware {
    @Override
    public void createToolWindowContent(@NotNull Project project, ToolWindow toolWindow) {
        RemoteSyncPanel panel = new RemoteSyncPanel(project);
        Content content = ContentFactory.getInstance().createContent(panel.getContent(), "", false);
        toolWindow.getContentManager().addContent(content);
    }
}
