package org.blacksoil.remotesync;

import static org.blacksoil.remotesync.util.UIUtils.addLabeledTextField;
import static org.blacksoil.remotesync.util.UIUtils.constraints;

import com.intellij.openapi.project.Project;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.ui.JBUI;
import java.awt.event.ActionEvent;
import java.util.Objects;
import javax.swing.*;

public class RemoteSyncPanel {
  private JPanel mainPanel;
  private JTextField usernameField;
  private JTextField hostField;
  private JTextField keyPathField;
  private JTextField remotePathField;
  private JTextField branchField;
  private JButton syncButton;
  private JLabel statusLabel;

  public RemoteSyncPanel(Project project) {
    initUI();

    RemoteSyncSettings settings = RemoteSyncSettings.getInstance(project);
    RemoteSyncSettings.State state = Objects.requireNonNull(settings.getState());

    loadStateToUI(state);
    setupSyncAction(project, settings);
  }

  private void initUI() {
    mainPanel = new JPanel();
    mainPanel.setLayout(new GridLayoutManager(7, 2, JBUI.insets(10), -1, -1));

    usernameField = addLabeledTextField(mainPanel, 0, "Username:");
    hostField = addLabeledTextField(mainPanel, 1, "Host/IP:");
    keyPathField = addLabeledTextField(mainPanel, 2, "Private Key Path:");
    remotePathField = addLabeledTextField(mainPanel, 3, "Remote Path:");
    branchField = addLabeledTextField(mainPanel, 4, "Git Branch:");

    syncButton = new JButton("Save & Sync");
    statusLabel = new JLabel("Status: Ready");

    mainPanel.add(syncButton, constraints(5, 1));
    mainPanel.add(statusLabel, constraints(6, 0, 2, true));
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
        (ActionEvent e) -> {
          setUiEnabled(false);
          updateStatus("Saving settings...");

          settings.applyFromUI(
              usernameField, hostField, keyPathField, remotePathField, branchField);
          RemoteSyncService.sync(project, settings.getState(), new RemoteSyncCallback());
        });
  }

  private void updateStatus(String message) {
    SwingUtilities.invokeLater(() -> statusLabel.setText(message));
  }

  private void setUiEnabled(boolean enabled) {
    SwingUtilities.invokeLater(() -> syncButton.setEnabled(enabled));
  }

  public JPanel getContent() {
    return mainPanel;
  }

  private class RemoteSyncCallback implements RemoteSyncService.SyncCallback {
    @Override
    public void onStatus(String message) {
      updateStatus(message);
    }

    @Override
    public void onError(String error) {
      updateStatus(error);
      setUiEnabled(true);
    }

    @Override
    public void onComplete() {
      setUiEnabled(true);
    }
  }
}
