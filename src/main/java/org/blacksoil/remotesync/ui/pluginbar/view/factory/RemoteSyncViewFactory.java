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
import lombok.experimental.UtilityClass;
import org.blacksoil.remotesync.ui.pluginbar.view.component.RemoteSyncViewComponents;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class RemoteSyncViewFactory {

  private static final int LABEL_LEFT_PAD = 12;

  public static RemoteSyncViewComponents create() {
    // --- поля ---
    JBTextField usernameField = new JBTextField();
    JBTextField ipField = new JBTextField();
    JBPasswordField passwordField = new JBPasswordField();
    JBTextField remotePathField = new JBTextField();
    JBTextField branchField = new JBTextField();

    // плейсхолдеры/подсказки
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

    // --- кнопки/индикатор/статус ---
    JButton testButton = new JButton("Test connection");
    JButton syncButton = new JButton("Save & Sync");
    JProgressBar progressBar = new JProgressBar();
    progressBar.setIndeterminate(true);
    progressBar.setVisible(false);

    JLabel statusLabel = new JLabel("Status: Ready");
    statusLabel.setBorder(JBUI.Borders.empty(6, 12, 12, 12));

    // действия строка
    JComponent actionsRow = buildActionsRow(testButton, progressBar, syncButton);

    // секции
    TitledSeparator sshSep = new TitledSeparator("Remote server");
    sshSep.setBorder(JBUI.Borders.empty(4, 12, 2, 12));
    TitledSeparator gitSep = new TitledSeparator("Git");
    gitSep.setBorder(JBUI.Borders.empty(8, 12, 2, 12));

    // лейблы с левым отступом
    JLabel userLabel = padLabel("Username");
    JLabel ipLabel = padLabel("IP address");
    JLabel passLabel = padLabel("Password");
    JLabel remoteLabel = padLabel("Git remote path");
    JLabel branchLabel = padLabel("Git branch");

    // корень формы
    JPanel root =
        FormBuilder.createFormBuilder()
            .addComponent(buildHeader())
            .addComponent(sshSep, 1)
            .addLabeledComponent(userLabel, usernameField, 1, false)
            .addLabeledComponent(ipLabel, ipField, 1, false)
            .addLabeledComponent(passLabel, passwordField, 1, false)
            .addComponent(gitSep, 1)
            .addLabeledComponent(remoteLabel, remotePathField, 1, false)
            .addLabeledComponent(branchLabel, branchField, 1, false)
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
        .testButton(testButton)
        .syncButton(syncButton)
        .progressBar(progressBar)
        .statusLabel(statusLabel)
        .build();
  }

  // ---------- private helpers ----------

  private static JLabel padLabel(String text) {
    JLabel l = new JLabel(text);
    l.setBorder(JBUI.Borders.emptyLeft(LABEL_LEFT_PAD));
    return l;
  }

  private static JComponent buildActionsRow(JButton test, JProgressBar progress, JButton save) {
    JBPanel<?> row = new JBPanel<>(new HorizontalLayout(8));
    save.putClientProperty("JButton.buttonType", "default");
    row.add(test);
    row.add(progress);
    row.add(save);
    return row;
  }

  private static JComponent buildHeader() {
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
