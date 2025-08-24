package org.blacksoil.remotesync.page.welcome.provider.browser;

import org.blacksoil.remotesync.page.welcome.browserAdapter.BrowserAdapter;

public interface BrowserProvider {
  boolean isSupported();

  BrowserAdapter create();
}
