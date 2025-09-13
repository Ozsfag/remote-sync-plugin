package org.blacksoil.remotesync.ui.welcome.listener;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.blacksoil.remotesync.ui.welcome.api.WelcomeFacade;
import org.blacksoil.remotesync.ui.welcome.constant.WelcomeConstants;
import org.blacksoil.remotesync.ui.welcome.impl.facade.DefaultWelcomeFacade;
import org.jetbrains.annotations.NotNull;

public record WelcomeProjectListener(WelcomeFacade services) implements ProjectManagerListener {

  /** Прод-конструктор (используется IDE). */
  public WelcomeProjectListener() {
    this(new DefaultWelcomeFacade());
  }

  /** Тестируемый конструктор. */
  public WelcomeProjectListener {}

  @Override
  @SuppressWarnings("removal")
  public void projectOpened(@NotNull Project project) {
    String version = services.getPluginVersionOrNull();
    if (version == null) return;

    String key = WelcomeConstants.buildWelcomeShownKey(version);
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
