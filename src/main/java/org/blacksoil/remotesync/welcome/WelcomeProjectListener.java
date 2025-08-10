package org.blacksoil.remotesync.welcome;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;

public final class WelcomeProjectListener implements ProjectManagerListener {
    private static final String PLUGIN_ID = "org.blacksoil.remotesync";

    @Override
    @SuppressWarnings("removal")
    public void projectOpened(@NotNull Project project) {
        IdeaPluginDescriptor d = PluginManagerCore.getPlugin(PluginId.getId(PLUGIN_ID));
        if (d == null) return;

        String key = "remoteSync.welcome.shown." + d.getVersion();
        PropertiesComponent pc = PropertiesComponent.getInstance(project);
        if (pc.getBoolean(key, false)) return;

        // дождёмся Smart Mode (аналог RequiredForSmartMode)
        DumbService.getInstance(project).runWhenSmart(() -> {
            if (project.isDisposed()) return;
            FileEditorManager.getInstance(project).openFile(new WelcomeVirtualFile(), true);
            pc.setValue(key, true);
        });
    }
}
