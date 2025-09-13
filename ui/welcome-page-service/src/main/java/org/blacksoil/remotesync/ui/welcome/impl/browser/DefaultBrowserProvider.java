package org.blacksoil.remotesync.ui.welcome.impl.browser;

import org.blacksoil.remotesync.ui.welcome.api.Browser;
import org.blacksoil.remotesync.ui.welcome.api.BrowserProvider;

public class DefaultBrowserProvider implements BrowserProvider {
  @Override
  public boolean isSupported() {
    return JcefBrowser.jcefSupported();
  }

  @Override
  public Browser create() {
    return new JcefBrowser();
  }
}
