package org.blacksoil.remotesync.ui.pluginbar.service;

public interface SyncCallback {
  void onStatus(String message);

  void onError(String error);

  void onComplete();
}
