package org.blacksoil.remotesync.ui.view;

import com.intellij.ui.JBColor;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Objects;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import lombok.Getter;
import org.blacksoil.remotesync.ui.model.FormData;
import org.blacksoil.remotesync.ui.view.component.RemoteSyncViewComponents;
import org.blacksoil.remotesync.ui.view.factory.RemoteSyncViewFactory;
import org.jetbrains.annotations.NotNull;

@Getter
public final class RemoteSyncView {

  private final JPanel root;

  // поля формы
  private final JTextField usernameField;
  private final JTextField ipField;
  private final JTextField passwordField; // JPasswordField совместим как JTextField
  private final JTextField remotePathField;
  private final JTextField branchField;

  // действия/статус
  private final JButton testButton;
  private final JButton syncButton;
  private final JProgressBar progressBar;
  private final JLabel statusLabel;

  // callbacks
  private Runnable onSync = () -> {};
  private Runnable onTest = () -> {};
  private Runnable onChange = () -> {};

  public RemoteSyncView() {
    RemoteSyncViewComponents c = RemoteSyncViewFactory.create();

    this.root = c.root();
    this.usernameField = c.usernameField();
    this.ipField = c.ipField();
    this.passwordField = c.passwordField();
    this.remotePathField = c.remotePathField();
    this.branchField = c.branchField();
    this.testButton = c.testButton();
    this.syncButton = c.syncButton();
    this.progressBar = c.progressBar();
    this.statusLabel = c.statusLabel();

    wireEnterAndChangeListeners();
    wireButtons();
  }

  // --- API ---

  public void setData(@NotNull FormData d) {
    usernameField.setText(n(d.username()));
    ipField.setText(n(d.ip()));
    passwordField.setText(n(d.password()));
    remotePathField.setText(n(d.remotePath()));
    branchField.setText(n(d.branch()));
  }

  public @NotNull FormData collectData() {
    return new FormData(
        usernameField.getText(),
        ipField.getText(),
        passwordField.getText(),
        remotePathField.getText(),
        branchField.getText());
  }

  public void setBusy(boolean busy) {
    syncButton.setEnabled(!busy);
    testButton.setEnabled(!busy);
    progressBar.setVisible(busy);
  }

  public void setStatus(String message, Color color) {
    statusLabel.setText("Status: " + message);
    statusLabel.setForeground(color != null ? color : JBColor.foreground());
  }

  /** Подсветка/сброс ошибки вокруг поля. */
  public void markError(JComponent c, boolean error) {
    c.putClientProperty("JComponent.outline", error ? "error" : null);
  }

  // callbacks
  public void onSync(Runnable r) {
    this.onSync = Objects.requireNonNull(r);
  }

  public void onTest(Runnable r) {
    this.onTest = Objects.requireNonNull(r);
  }

  public void onChange(Runnable r) {
    this.onChange = Objects.requireNonNull(r);
  }

  // --- wiring ---

  private void wireButtons() {
    syncButton.addActionListener(e -> onSync.run());
    testButton.addActionListener(e -> onTest.run());
  }

  private void wireEnterAndChangeListeners() {
    ActionListener onEnter = e -> onSync.run();
    usernameField.addActionListener(onEnter);
    ipField.addActionListener(onEnter);
    passwordField.addActionListener(onEnter);
    remotePathField.addActionListener(onEnter);
    branchField.addActionListener(onEnter);

    DocumentListener dl = new SimpleDocListener(() -> onChange.run());
    usernameField.getDocument().addDocumentListener(dl);
    ipField.getDocument().addDocumentListener(dl);
    passwordField.getDocument().addDocumentListener(dl);
    remotePathField.getDocument().addDocumentListener(dl);
    branchField.getDocument().addDocumentListener(dl);
  }

  // helper
  private record SimpleDocListener(Runnable r) implements DocumentListener {
    @Override
    public void insertUpdate(DocumentEvent e) {
      r.run();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
      r.run();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
      r.run();
    }
  }

  private static String n(String v) {
    return v == null ? "" : v;
  }
}
