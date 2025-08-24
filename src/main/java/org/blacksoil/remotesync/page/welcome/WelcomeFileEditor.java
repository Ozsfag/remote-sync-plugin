package org.blacksoil.remotesync.page.welcome;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBScrollPane;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.*;
import org.blacksoil.remotesync.page.welcome.browserAdapter.BrowserAdapter;
import org.blacksoil.remotesync.page.welcome.htmlLoader.*;
import org.blacksoil.remotesync.page.welcome.provider.browser.BrowserProvider;
import org.blacksoil.remotesync.page.welcome.provider.browser.DefaultBrowserProvider;
import org.blacksoil.remotesync.page.welcome.provider.version.IntellijPluginVersionProvider;
import org.blacksoil.remotesync.page.welcome.provider.version.PluginVersionProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class WelcomeFileEditor extends UserDataHolderBase implements FileEditor {
  private final JPanel panel;
  private final JComponent focus;
  private final VirtualFile file;
  private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

  // Прод-конструктор (обратная совместимость)
  public WelcomeFileEditor(@NotNull VirtualFile file) {
    this(
        file,
        new DefaultBrowserProvider(),
        new ClasspathHtmlLoader(),
        new IntellijPluginVersionProvider());
  }

  // Тестируемый конструктор (DI)
  public WelcomeFileEditor(
      @NotNull VirtualFile file,
      @NotNull BrowserProvider browserProvider,
      @NotNull HtmlLoader htmlLoader,
      @NotNull PluginVersionProvider versionProvider) {
    this.file = file;
    this.panel = new JPanel(new BorderLayout());
    String version = versionProvider.getVersionOrDefault();

    String html =
        firstNotNull(
            htmlLoader.loadOrNull("welcome/welcome.html"),
            htmlLoader.loadOrNull("welcome/fallback.html"));
    if (html != null) {
      html = html.replace("${version}", version);
    }

    if (browserProvider.isSupported()) {
      // nullable в режиме fallback
      BrowserAdapter browserAdapter = browserProvider.create();
      if (html != null) browserAdapter.loadHtml(html);
      focus = browserAdapter.getComponent();
    } else {
      // Swing fallback
      JEditorPane pane = new JEditorPane();
      pane.setEditable(false);
      pane.setContentType("text/html");
      if (html != null) pane.setText(html);
      focus = new JBScrollPane(pane);
    }

    panel.add(focus, BorderLayout.CENTER);
  }

  private static String firstNotNull(String a, String b) {
    return a != null ? a : b;
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
    /* браузер диспоузится GC-ом; JCEF управляется IDE */
  }

  @Override
  public @NotNull VirtualFile getFile() {
    return file;
  }
}
