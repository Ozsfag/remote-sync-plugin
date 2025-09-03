package org.blacksoil.remotesync.ui.welcomepage.impl.browser;

import com.intellij.ui.jcef.JBCefApp;
import com.intellij.ui.jcef.JBCefBrowser;
import javax.swing.*;
import lombok.Getter;
import org.blacksoil.remotesync.ui.welcomepage.api.Browser;

public class JcefBrowser implements Browser {
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
