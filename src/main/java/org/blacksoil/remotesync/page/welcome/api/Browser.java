package org.blacksoil.remotesync.page.welcome.api;

import javax.swing.*;

public interface Browser {
  JComponent getComponent();

  void loadHtml(String html);
}
