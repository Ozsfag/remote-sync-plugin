package org.blacksoil.remotesync.ui.welcomepage.impl.browser;

import org.blacksoil.remotesync.ui.welcomepage.api.Browser;
import org.blacksoil.remotesync.ui.welcomepage.api.BrowserProvider;

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
