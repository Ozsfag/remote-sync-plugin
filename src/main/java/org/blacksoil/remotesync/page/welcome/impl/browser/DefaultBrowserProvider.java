package org.blacksoil.remotesync.page.welcome.impl.browser;

import org.blacksoil.remotesync.page.welcome.api.Browser;
import org.blacksoil.remotesync.page.welcome.api.BrowserProvider;

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
