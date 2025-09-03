package org.blacksoil.remotesync.ui.pluginbar.app;

public interface TaskCallback {
  void onStatus(String message);

  void onError(String error);

  void onComplete();
}
