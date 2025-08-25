package org.blacksoil.remotesync.ui.welcomepage.api;

public interface BrowserProvider {
  boolean isSupported();

  Browser create();
}
