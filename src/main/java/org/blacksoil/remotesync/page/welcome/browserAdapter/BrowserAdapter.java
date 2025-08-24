package org.blacksoil.remotesync.page.welcome.browserAdapter;

import javax.swing.*;

public interface BrowserAdapter {
  JComponent getComponent();

  void loadHtml(String html);
}
