package org.blacksoil.remotesync.ui.welcomepage.ui;

import static org.blacksoil.remotesync.ui.welcomepage.WelcomeConstants.*;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBScrollPane;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.*;
import org.blacksoil.remotesync.ui.welcomepage.api.Browser;
import org.blacksoil.remotesync.ui.welcomepage.api.BrowserProvider;
import org.blacksoil.remotesync.ui.welcomepage.api.HtmlLoader;
import org.blacksoil.remotesync.ui.welcomepage.api.PluginVersionProvider;
import org.blacksoil.remotesync.ui.welcomepage.impl.browser.DefaultBrowserProvider;
import org.blacksoil.remotesync.ui.welcomepage.impl.resource.ClasspathHtmlLoader;
import org.blacksoil.remotesync.ui.welcomepage.impl.version.IntellijPluginVersionProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class WelcomeFileEditor extends UserDataHolderBase implements FileEditor {
  private final JPanel panel;
  private final JComponent focus;
  private final VirtualFile file;
  private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

  // Прод-конструктор
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
    String primary = htmlLoader.loadOrNull(PRIMARY_HTML_PATH);
    String fallback = htmlLoader.loadOrNull(FALLBACK_HTML_PATH);
    String html = applyVersion(pickHtmlOrDefault(primary, fallback), version);

    Browser browserOrNull;
    if (browserProvider.isSupported()) {
      Browser b = browserProvider.create();
      b.loadHtml(html);
      this.focus = b.getComponent();
    } else {
      JEditorPane pane = new JEditorPane();
      pane.setEditable(false);
      pane.setContentType("text/html");
      pane.setText(html);
      this.focus = new JBScrollPane(pane);
    }

    this.panel.add(focus, BorderLayout.CENTER);
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
    /* если нужно диспозить JCEF – делай это внутри BrowserProvider */
  }

  @Override
  public @NotNull VirtualFile getFile() {
    return file;
  }
}
