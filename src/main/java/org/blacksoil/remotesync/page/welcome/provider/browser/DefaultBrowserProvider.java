package org.blacksoil.remotesync.page.welcome.provider.browser;

import org.blacksoil.remotesync.page.welcome.browserAdapter.BrowserAdapter;
import org.blacksoil.remotesync.page.welcome.browserAdapter.JcefBrowserAdapter;

public class DefaultBrowserProvider implements BrowserProvider {
  @Override
  public boolean isSupported() {
    return JcefBrowserAdapter.jcefSupported();
  }

  @Override
  public BrowserAdapter create() {
    return new JcefBrowserAdapter();
  }
}
