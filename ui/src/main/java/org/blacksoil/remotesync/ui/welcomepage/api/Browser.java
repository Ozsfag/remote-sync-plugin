package org.blacksoil.remotesync.ui.welcomepage.api;

import javax.swing.*;

public interface Browser {
  JComponent getComponent();

  void loadHtml(String html);
}
