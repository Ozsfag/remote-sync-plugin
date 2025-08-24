package org.blacksoil.remotesync.page.welcome.impl.facade;

import static org.blacksoil.remotesync.page.welcome.WelcomeConstants.*;

import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import java.util.function.Supplier;
import org.blacksoil.remotesync.page.welcome.api.WelcomeFacade;
import org.blacksoil.remotesync.page.welcome.vfs.WelcomeVirtualFile;

public record DefaultWelcomeFacade(Supplier<VirtualFile> fileFactory) implements WelcomeFacade {

  /** Дополнительный конструктор без аргументов — делегирует в канонический. */
  public DefaultWelcomeFacade() {
    this(WelcomeVirtualFile::new);
  }

  @Override
  public String getPluginVersionOrNull() {
    var d = PluginManagerCore.getPlugin(PluginId.getId(PLUGIN_ID));
    return d != null ? d.getVersion() : null;
  }

  @Override
  public boolean isShown(Project project, String key) {
    return PropertiesComponent.getInstance(project).getBoolean(key, false);
  }

  @Override
  public void setShown(Project project, String key, boolean value) {
    PropertiesComponent.getInstance(project).setValue(key, value);
  }

  @Override
  public void runWhenSmart(Project project, Runnable r) {
    DumbService.getInstance(project).runWhenSmart(r);
  }

  @Override
  public void openWelcome(Project project) {
    FileEditorManager.getInstance(project).openFile(fileFactory.get(), true);
  }
}
