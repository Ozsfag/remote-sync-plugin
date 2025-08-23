package org.blacksoil.remotesync.ui.view;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.JBColor;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.AnActionLink;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.components.panels.HorizontalLayout;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Objects;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.blacksoil.remotesync.ui.model.FormData;
import org.jetbrains.annotations.NotNull;

public final class RemoteSyncView {

  // --- colors ---
  private static final int LABEL_LEFT_PAD = 12;

  // --- UI components ---
  private final JPanel root;

  private final JBTextField usernameField = new JBTextField();
  private final JBTextField ipField = new JBTextField();
  private final JBPasswordField passwordField = new JBPasswordField();
  private final JBTextField remotePathField = new JBTextField();
  private final JBTextField branchField = new JBTextField();

  private final JButton testButton = new JButton("Test connection");
  private final JButton syncButton = new JButton("Save & Sync");
  private final JProgressBar progressBar = new JProgressBar();
  private final JLabel statusLabel = new JLabel("Status: Ready");

  // callbacks
  private Runnable onSync = () -> {};
  private Runnable onTest = () -> {};
  private Runnable onChange = () -> {};

  public RemoteSyncView() {
    configurePlaceholders();
    configureEnterShortcut();

    progressBar.setIndeterminate(true);
    progressBar.setVisible(false);

    root = buildForm();
  }

  // --- public API ---

  private static JLabel padLabel(String text) {
    JLabel l = new JLabel(text);
    l.setBorder(JBUI.Borders.emptyLeft(LABEL_LEFT_PAD));
    return l;
  }

  private static String s(String v) {
    return v == null ? "" : v;
  }

  public JComponent getComponent() {
    return root;
  }

  public void setData(FormData d) {
    // record‑аксессоры: username(), ip(), …
    usernameField.setText(s(d.username()));
    ipField.setText(s(d.ip()));
    passwordField.setText(s(d.password()));
    remotePathField.setText(s(d.remotePath()));
    branchField.setText(s(d.branch()));
  }

  public FormData collectData() {
    // ✅ record: используем конструктор с 5 аргументами
    return new FormData(
        usernameField.getText(),
        ipField.getText(),
        new String(passwordField.getPassword()),
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

  public JTextField usernameField() {
    return usernameField;
  }

  public JTextField ipField() {
    return ipField;
  }

  public JTextField passwordField() {
    return passwordField;
  } // JPasswordField extends JTextField

  public JTextField remotePathField() {
    return remotePathField;
  }

  public JTextField branchField() {
    return branchField;
  }

  public void onSync(Runnable r) {
    this.onSync = Objects.requireNonNull(r);
  }

  // --- internal: view build ---

  public void onTest(Runnable r) {
    this.onTest = Objects.requireNonNull(r);
  }

  public void onChange(Runnable r) {
    this.onChange = Objects.requireNonNull(r);
  }

  private JPanel buildForm() {
    // labels with left padding
    JLabel userLabel = padLabel("Username");
    JLabel ipLabel = padLabel("IP address");
    JLabel passLabel = padLabel("Password");
    JLabel remoteLabel = padLabel("Git remote path");
    JLabel branchLabel = padLabel("Git branch");
    statusLabel.setBorder(JBUI.Borders.empty(6, 12, 12, 12));

    TitledSeparator sshSep = new TitledSeparator("Remote server");
    sshSep.setBorder(JBUI.Borders.empty(4, 12, 2, 12));

    TitledSeparator gitSep = new TitledSeparator("Git");
    gitSep.setBorder(JBUI.Borders.empty(8, 12, 2, 12));

    return FormBuilder.createFormBuilder()
        .addComponent(buildHeader())
        .addComponent(sshSep, 1)
        .addLabeledComponent(userLabel, usernameField, 1, false)
        .addLabeledComponent(ipLabel, ipField, 1, false)
        .addLabeledComponent(passLabel, passwordField, 1, false)
        .addComponent(gitSep, 1)
        .addLabeledComponent(remoteLabel, remotePathField, 1, false)
        .addLabeledComponent(branchLabel, branchField, 1, false)
        .addComponentToRightColumn(buildActionsRow(), 1)
        .addComponent(statusLabel)
        .getPanel();
  }

  private JComponent buildHeader() {
    JPanel header = new JPanel();
    header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));
    header.setBorder(JBUI.Borders.empty(6, 12));

    // цвет ссылок — стандартный акцент IDE
    Color linkColor =
        JBColor.namedColor(
            "Link.activeForeground", new JBColor(new Color(0x3D7EFF), new Color(0x3D7EFF)));

    AnActionLink report =
        new AnActionLink(
            "Report issue",
            new AnAction() {
              @Override
              public void actionPerformed(@NotNull AnActionEvent e) {
                BrowserUtil.browse("https://github.com/Ozsfag/remote-sync-plugin/issues");
              }
            });
    report.setFont(JBFont.small());
    report.setForeground(linkColor);

    JLabel dot = new JLabel(" · ");
    dot.setFont(JBFont.small());
    dot.setForeground(JBColor.namedColor("Label.infoForeground", JBColor.gray));

    AnActionLink sponsor =
        new AnActionLink(
            "Support development ❤",
            new AnAction() {
              @Override
              public void actionPerformed(@NotNull AnActionEvent e) {
                BrowserUtil.browse("https://boosty.to/ozsfag/donate");
              }
            });
    sponsor.setFont(JBFont.small());
    sponsor.setForeground(linkColor);

    header.add(report);
    header.add(dot);
    header.add(sponsor);
    header.add(Box.createHorizontalGlue());
    return header;
  }

  private JComponent buildActionsRow() {
    JBPanel<?> row = new JBPanel<>(new HorizontalLayout(8));
    syncButton.putClientProperty("JButton.buttonType", "default"); // primary by LAF
    row.add(testButton);
    row.add(progressBar);
    row.add(syncButton);

    syncButton.addActionListener(e -> onSync.run());
    testButton.addActionListener(e -> onTest.run());
    return row;
  }

  private void configurePlaceholders() {
    usernameField.getEmptyText().setText("admin");
    usernameField.setToolTipText("Username for SSH login");

    ipField.getEmptyText().setText("192.168.1.100");
    ipField.setToolTipText("Remote server IP or hostname");

    passwordField.getEmptyText().setText("Username password");
    passwordField.setToolTipText("password");

    remotePathField.getEmptyText().setText("git@github.com:client/project.git");
    remotePathField.setToolTipText("Remote directory or Git remote");

    branchField.getEmptyText().setText("main");
    branchField.setToolTipText("Git branch to compare changes against (e.g., main)");
  }

  private void configureEnterShortcut() {
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

  // small helper to avoid boilerplate
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
}
