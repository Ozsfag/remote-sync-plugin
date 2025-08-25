package org.blacksoil.remotesync.ui.pluginbar.service;

import com.intellij.ui.JBColor;
import java.awt.*;
import javax.swing.*;
import org.blacksoil.remotesync.ui.pluginbar.view.RemoteSyncView;

/** Централизованная работа со статусной строкой. */
public record StatusReporter(RemoteSyncView view) {

  public void info(String msg) {
    set(msg, null);
  }

  public void ok(String msg) {
    set(msg, JBColor.namedColor("Notifications.Green", JBColor.GREEN));
  }

  public void error(String msg) {
    set(msg, JBColor.namedColor("Notifications.Red", JBColor.RED));
  }

  public void set(String msg, Color color) {
    // Safety: всегда на EDT
    if (SwingUtilities.isEventDispatchThread()) {
      view.setStatus(msg, color);
    } else {
      SwingUtilities.invokeLater(() -> view.setStatus(msg, color));
    }
  }
}
