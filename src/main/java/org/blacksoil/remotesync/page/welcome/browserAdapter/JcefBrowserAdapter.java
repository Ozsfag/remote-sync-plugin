package org.blacksoil.remotesync.page.welcome.browserAdapter;

import com.intellij.ui.jcef.JBCefApp;
import com.intellij.ui.jcef.JBCefBrowser;
import javax.swing.*;
import lombok.Getter;

public class JcefBrowserAdapter implements BrowserAdapter {
  @Getter private final JBCefBrowser inner = new JBCefBrowser();

  public static boolean jcefSupported() {
    return JBCefApp.isSupported();
  }

  @Override
  public JComponent getComponent() {
    return inner.getComponent();
  }

  @Override
  public void loadHtml(String html) {
    inner.loadHTML(html);
  }
}
