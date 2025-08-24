package org.blacksoil.remotesync.page.welcome;

import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.jcef.JBCefApp;
import com.intellij.ui.jcef.JBCefBrowser;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class WelcomeFileEditor extends UserDataHolderBase implements FileEditor {
  private static final String PLUGIN_ID = "org.blacksoil.remotesync";

  private final JPanel panel;
  private final JComponent focus;
  private final VirtualFile file;
  private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
  private JBCefBrowser browser;

  public WelcomeFileEditor(@NotNull VirtualFile file) {
    this.file = file;
    this.panel = new JPanel(new BorderLayout());
    String version = pluginVersion();

    URL welcomeUrl = getRes("welcome/welcome.html");
    URL fallbackUrl = getRes("welcome/fallback.html");

    if (JBCefApp.isSupported()) {
      browser = new JBCefBrowser();
      Disposer.register(this, browser);

      String html = readOrNull(getRes("welcome/welcome.html"));
      if (html == null) html = readOrNull(getRes("welcome/fallback.html"));
      if (html != null) {
        html = html.replace("${version}", version);
        browser.loadHTML(html); // ✅ вот ключевая часть
      }

      focus = browser.getComponent();
    } else {
      // Swing fallback
      JEditorPane pane = new JEditorPane();
      pane.setEditable(false);
      pane.setContentType("text/html");

      String html = readOrNull(welcomeUrl);
      if (html == null) html = readOrNull(fallbackUrl);
      if (html != null) {
        html = html.replace("${version}", version);
        pane.setText(html);
      }

      focus = new JBScrollPane(pane);
    }

    panel.add(focus, BorderLayout.CENTER);
  }

  private static @Nullable URL getRes(String path) {
    return WelcomeFileEditor.class.getClassLoader().getResource(path);
  }

  private static String readOrNull(@Nullable URL url) {
    if (url == null) return null;
    try (InputStream is = url.openStream()) {
      return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    } catch (Exception ignored) {
      return null;
    }
  }

  private static String pluginVersion() {
    var d = PluginManagerCore.getPlugin(PluginId.getId(PLUGIN_ID));
    return d != null ? d.getVersion() : "0.0.0";
  }

  @Override
  public @NotNull JComponent getComponent() {
    return panel;
  }

  @Override
  public @Nullable JComponent getPreferredFocusedComponent() {
    return focus;
  }

  @Override
  public @NotNull String getName() {
    return "Welcome";
  }

  @Override
  public void setState(@NotNull FileEditorState state) {}

  @Override
  public boolean isModified() {
    return false;
  }

  @Override
  public boolean isValid() {
    return true;
  }

  @Override
  public void addPropertyChangeListener(@NotNull PropertyChangeListener l) {
    pcs.addPropertyChangeListener(l);
  }

  @Override
  public void removePropertyChangeListener(@NotNull PropertyChangeListener l) {
    pcs.removePropertyChangeListener(l);
  }

  @Override
  public void dispose() {
    if (browser != null) {
      browser.dispose();
      browser = null;
    }
  }

  @Override
  public @NotNull VirtualFile getFile() {
    return file;
  }
}
