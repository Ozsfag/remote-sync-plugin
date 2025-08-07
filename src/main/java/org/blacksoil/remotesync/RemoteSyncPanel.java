package org.blacksoil.remotesync;

import static org.blacksoil.remotesync.util.UIUtils.addLabeledTextField;
import static org.blacksoil.remotesync.util.UIUtils.constraints;

import com.intellij.openapi.project.Project;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.ui.JBUI;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Objects;
import javax.swing.*;
import org.blacksoil.remotesync.service.RemoteSyncService;
import org.blacksoil.remotesync.settings.RemoteSyncSettings;
import org.blacksoil.remotesync.validator.PanelValidator;

public class RemoteSyncPanel {
  private JPanel mainPanel;
  private JTextField usernameField;
  private JTextField hostField;
  private JTextField keyPathField;
  private JTextField remotePathField;
  private JTextField branchField;
  private JButton syncButton;
  private JLabel statusLabel;
  private JProgressBar progressBar;

  public RemoteSyncPanel(Project project) {
    initUI();
    applyLookAndFeel(); // <-- FlatLaf
    setToolTips();

    RemoteSyncSettings settings = RemoteSyncSettings.getInstance(project);
    RemoteSyncSettings.State state = Objects.requireNonNull(settings.getState());

    loadStateToUI(state);
    setupSyncAction(project, settings);
  }

  private void initUI() {
    mainPanel = new JPanel(new GridLayoutManager(8, 2, JBUI.insets(10), -1, -1));

    usernameField = addLabeledTextField(mainPanel, 0, "Username:");
    hostField = addLabeledTextField(mainPanel, 1, "IP:");
    keyPathField = addLabeledTextField(mainPanel, 2, "Password:");
    remotePathField = addLabeledTextField(mainPanel, 3, "Git Remote Path:");
    branchField = addLabeledTextField(mainPanel, 4, "Git Branch:");

    syncButton = new JButton("Save & Sync");
    statusLabel = new JLabel("Status: Ready");

    progressBar = new JProgressBar();
    progressBar.setIndeterminate(true);
    progressBar.setVisible(false);

    mainPanel.add(progressBar, constraints(5, 0));
    mainPanel.add(syncButton, constraints(5, 1));
    mainPanel.add(statusLabel, constraints(6, 0, 2, true));

    setupEnterKeyShortcut();
  }

  private void applyLookAndFeel() {
    try {
      UIManager.setLookAndFeel(UIManager.getLookAndFeel()); // respect IntelliJ theme
      SwingUtilities.updateComponentTreeUI(mainPanel);
    } catch (Exception ignored) {
    }
  }

  private void setToolTips() {
    usernameField.setToolTipText("Username for SSH login");
    hostField.setToolTipText("Remote server IP or hostname");
    keyPathField.setToolTipText("Path to your private SSH key");
    remotePathField.setToolTipText("Remote directory where files will be synced");
    branchField.setToolTipText("Git branch to compare changes against (e.g., main)");
  }

  private void loadStateToUI(RemoteSyncSettings.State state) {
    usernameField.setText(state.username);
    hostField.setText(state.host);
    keyPathField.setText(state.privateKeyPath);
    remotePathField.setText(state.remotePath);
    branchField.setText(state.branch != null ? state.branch : "main");
  }

  private void setupSyncAction(Project project, RemoteSyncSettings settings) {
    syncButton.addActionListener(
        e -> {
          updateStatus("Saving settings...");
          setUiEnabled(false);

          if (!PanelValidator.isValid(
              usernameField, hostField, keyPathField, remotePathField, branchField)) {
            updateStatus("Please fill in all required fields.");
            setUiEnabled(true);
            return;
          }

          settings.applyFromUI(
              usernameField, hostField, keyPathField, remotePathField, branchField);
          setCursorWait(true);

          RemoteSyncService.sync(
              project,
              settings.getState(),
              new RemoteSyncService.SyncCallback() {
                @Override
                public void onStatus(String msg) {
                  updateStatus(msg);
                }

                @Override
                public void onError(String err) {
                  updateStatus(err);
                  setUiEnabled(true);
                  setCursorWait(false);
                }

                @Override
                public void onComplete() {
                  setUiEnabled(true);
                  setCursorWait(false);
                }
              });
        });
  }

  private void setupEnterKeyShortcut() {
    ActionListener enterListener =
        e -> {
          if (syncButton.isEnabled()) syncButton.doClick();
        };

    usernameField.addActionListener(enterListener);
    hostField.addActionListener(enterListener);
    keyPathField.addActionListener(enterListener);
    remotePathField.addActionListener(enterListener);
    branchField.addActionListener(enterListener);
  }

  private void updateStatus(String message) {
    SwingUtilities.invokeLater(() -> statusLabel.setText(message));
  }

  private void setUiEnabled(boolean enabled) {
    SwingUtilities.invokeLater(
        () -> {
          syncButton.setEnabled(enabled);
          progressBar.setVisible(!enabled);
        });
  }

  private void setCursorWait(boolean wait) {
    Cursor cursor =
        wait ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor();
    SwingUtilities.invokeLater(() -> mainPanel.setCursor(cursor));
  }

  public JPanel getContent() {
    return mainPanel;
  }
}
