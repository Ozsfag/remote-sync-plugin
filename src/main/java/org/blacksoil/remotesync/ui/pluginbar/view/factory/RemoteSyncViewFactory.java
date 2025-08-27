package org.blacksoil.remotesync.ui.pluginbar.view.factory;

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
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import lombok.experimental.UtilityClass;
import org.blacksoil.remotesync.ui.pluginbar.view.component.RemoteSyncViewComponents;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class RemoteSyncViewFactory {

  private final int LABEL_LEFT_PAD = 12;

  public RemoteSyncViewComponents create() {
    JBTextField usernameField = new JBTextField();
    JBTextField ipField = new JBTextField();
    JBPasswordField passwordField = new JBPasswordField();
    JBTextField remotePathField = new JBTextField();
    JBTextField branchField = new JBTextField();
    JBTextField gitUrlField = new JBTextField();

    usernameField.getEmptyText().setText("admin");
    ipField.getEmptyText().setText("192.168.1.100");
    passwordField.getEmptyText().setText("SSH password");
    remotePathField.getEmptyText().setText("~/project");
    branchField.getEmptyText().setText("main");
    gitUrlField.getEmptyText().setText("https://github.com/user/repo.git");
    gitUrlField.setToolTipText("Git remote URL (auto-fills remote path)");

    gitUrlField
        .getDocument()
        .addDocumentListener(
            new DocumentListener() {
              private void update() {
                String url = gitUrlField.getText();
                if (url.endsWith(".git")) url = url.substring(0, url.length() - 4);
                if (url.contains("/")) {
                  String repo = url.substring(url.lastIndexOf('/') + 1);
                  remotePathField.setText("~/" + repo);
                }
              }

              public void insertUpdate(DocumentEvent e) {
                update();
              }

              public void removeUpdate(DocumentEvent e) {
                update();
              }

              public void changedUpdate(DocumentEvent e) {
                update();
              }
            });

    JButton testButton = new JButton("Test connection");
    JButton syncButton = new JButton("Save & Sync");
    JProgressBar progressBar = new JProgressBar();
    progressBar.setIndeterminate(true);
    progressBar.setVisible(false);

    JLabel statusLabel = new JLabel("Status: Ready");
    statusLabel.setBorder(JBUI.Borders.empty(6, 12, 12, 12));

    JComponent actionsRow = buildActionsRow(testButton, progressBar, syncButton);

    TitledSeparator sshSep = new TitledSeparator("Remote server");
    sshSep.setBorder(JBUI.Borders.empty(4, 12, 2, 12));
    TitledSeparator gitSep = new TitledSeparator("Git");
    gitSep.setBorder(JBUI.Borders.empty(8, 12, 2, 12));

    JPanel root =
        FormBuilder.createFormBuilder()
            .addComponent(buildHeader())
            .addComponent(sshSep, 1)
            .addLabeledComponent(padLabel("Username"), usernameField, 1, false)
            .addLabeledComponent(padLabel("IP address"), ipField, 1, false)
            .addLabeledComponent(padLabel("Password"), passwordField, 1, false)
            .addComponent(gitSep, 1)
            .addLabeledComponent(padLabel("Git remote URL"), gitUrlField, 1, false)
            .addLabeledComponent(padLabel("Server remote path"), remotePathField, 1, false)
            .addLabeledComponent(padLabel("Git branch"), branchField, 1, false)
            .addComponentToRightColumn(actionsRow, 1)
            .addComponent(statusLabel)
            .getPanel();

    return RemoteSyncViewComponents.builder()
        .root(root)
        .usernameField(usernameField)
        .ipField(ipField)
        .passwordField(passwordField)
        .remotePathField(remotePathField)
        .branchField(branchField)
        .gitUrlField(gitUrlField)
        .testButton(testButton)
        .syncButton(syncButton)
        .progressBar(progressBar)
        .statusLabel(statusLabel)
        .build();
  }

  private JLabel padLabel(String text) {
    JLabel l = new JLabel(text);
    l.setBorder(JBUI.Borders.emptyLeft(LABEL_LEFT_PAD));
    return l;
  }

  private JComponent buildActionsRow(JButton test, JProgressBar progress, JButton save) {
    JBPanel<?> row = new JBPanel<>(new HorizontalLayout(8));
    save.putClientProperty("JButton.buttonType", "default");
    row.add(test);
    row.add(progress);
    row.add(save);
    return row;
  }

  private JComponent buildHeader() {
    JPanel header = new JPanel();
    header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));
    header.setBorder(JBUI.Borders.empty(6, 12));

    Color linkColor =
        JBColor.namedColor(
            "Link.activeForeground", new JBColor(new Color(0x3D7EFF), new Color(0x3D7EFF)));

    AnActionLink report =
        new AnActionLink(
            "🐞 Report issue",
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
}
