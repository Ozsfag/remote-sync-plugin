package org.blacksoil.remotesync.ui.welcome.api;

import javax.swing.*;

public interface Browser {
  JComponent getComponent();

  void loadHtml(String html);
}
