package org.blacksoil.remotesync.ui.toolbar.app;

public interface TaskCallback {
  void onStatus(String message);

  void onError(String error);

  void onComplete();
}
