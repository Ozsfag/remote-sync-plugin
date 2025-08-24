package org.blacksoil.remotesync.page.welcome.api;

import com.intellij.openapi.project.Project;

public interface WelcomeFacade {
  String getPluginVersionOrNull();

  boolean isShown(Project project, String key);

  void setShown(Project project, String key, boolean value);

  void runWhenSmart(Project project, Runnable r);

  void openWelcome(Project project);
}
