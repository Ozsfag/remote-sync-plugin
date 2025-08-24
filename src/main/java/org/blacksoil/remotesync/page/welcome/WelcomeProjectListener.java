package org.blacksoil.remotesync.page.welcome;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.blacksoil.remotesync.page.welcome.service.DefaultWelcomeFacadeServices;
import org.blacksoil.remotesync.page.welcome.service.WelcomeFacadeServices;
import org.jetbrains.annotations.NotNull;

public record WelcomeProjectListener(WelcomeFacadeServices services)
    implements ProjectManagerListener {

  /** Прод-конструктор (используется IDE). */
  public WelcomeProjectListener() {
    this(new DefaultWelcomeFacadeServices());
  }

  /** Тестируемый конструктор. */
  public WelcomeProjectListener {}

  static String buildKey(String version) {
    return "remoteSync.welcome.shown." + version;
  }

  @Override
  @SuppressWarnings("removal")
  public void projectOpened(@NotNull Project project) {
    String version = services.getPluginVersionOrNull();
    if (version == null) return;

    String key = buildKey(version);
    if (services.isShown(project, key)) return;

    services.runWhenSmart(
        project,
        () -> {
          if (project.isDisposed()) return;
          services.openWelcome(project);
          services.setShown(project, key, true);
        });
  }
}
