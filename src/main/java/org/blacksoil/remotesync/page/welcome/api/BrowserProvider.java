package org.blacksoil.remotesync.page.welcome.api;

public interface BrowserProvider {
  boolean isSupported();

  Browser create();
}
