package org.blacksoil.remotesync.ui.welcome.api;

public interface BrowserProvider {
  boolean isSupported();

  Browser create();
}
