package org.blacksoil.remotesync.ui.pluginbar.service.task.api;

public interface TaskCallback {
  void onStatus(String message);

  void onError(String error);

  void onComplete();
}
