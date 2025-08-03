package org.blacksoil.remotesync;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.JBUI;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Objects;
import javax.swing.*;

public class RemoteSyncPanel {
  private static final Logger LOG = Logger.getInstance(RemoteSyncPanel.class);

  private JPanel mainPanel;
  private JTextField usernameField;
  private JTextField hostField;
  private JTextField keyPathField;
  private JTextField remotePathField;
  private JTextField branchField;
  private JButton syncButton;
  private JLabel statusLabel;

  public RemoteSyncPanel(Project project) {
    $$$setupUI$$$();

    RemoteSyncSettings settings = RemoteSyncSettings.getInstance(project);
    RemoteSyncSettings.State state = Objects.requireNonNull(settings.getState());

    // Установка сохранённых значений
    usernameField.setText(state.username);
    hostField.setText(state.host);
    keyPathField.setText(state.privateKeyPath);
    remotePathField.setText(state.remotePath);
    branchField.setText(state.branch != null ? state.branch : "main");

    syncButton.addActionListener((ActionEvent e) -> performSync(project, state, settings));
  }

  public JPanel getContent() {
    return mainPanel;
  }

  private void performSync(
      Project project, RemoteSyncSettings.State state, RemoteSyncSettings settings) {
    new Thread(
            () -> {
              setUiEnabled(false);
              try {
                updateStatus("Saving settings...");

                // Сохранение новых данных
                state.username = usernameField.getText().trim();
                state.host = hostField.getText().trim();
                state.privateKeyPath = keyPathField.getText().trim();
                state.remotePath = remotePathField.getText().trim();
                state.branch = branchField.getText().trim();

                settings.loadState(state);

                updateStatus("Detecting changes...");

                GitDiffDetector.DiffResult diff =
                    GitDiffDetector.getChangedFiles(project.getBasePath(), state.branch);
                List<String> addedOrModified = diff.addedOrModified();
                List<String> deleted = diff.deleted();

                if (addedOrModified.isEmpty() && deleted.isEmpty()) {
                  updateStatus("No changes.");
                  return;
                }

                if (!addedOrModified.isEmpty()) {
                  updateStatus("Uploading " + addedOrModified.size() + " file(s)...");
                  SshUploader.uploadFiles(
                      addedOrModified,
                      project.getBasePath(),
                      state.remotePath,
                      state.host,
                      state.username,
                      state.privateKeyPath);
                }

                if (!deleted.isEmpty()) {
                  updateStatus("Deleting " + deleted.size() + " file(s)...");
                  SshUploader.deleteFiles(
                      deleted, state.remotePath, state.host, state.username, state.privateKeyPath);
                }

                updateStatus("Sync complete.");
              } catch (Exception ex) {
                LOG.error("Sync failed", ex);
                updateStatus("Error: " + ex.getMessage());
              } finally {
                setUiEnabled(true);
              }
            })
        .start();
  }

  private void updateStatus(String message) {
    SwingUtilities.invokeLater(() -> statusLabel.setText(message));
  }

  private void setUiEnabled(boolean enabled) {
    SwingUtilities.invokeLater(() -> syncButton.setEnabled(enabled));
  }

  private void $$$setupUI$$$() {
    mainPanel = new JPanel();
    mainPanel.setLayout(
        new com.intellij.uiDesigner.core.GridLayoutManager(7, 2, JBUI.insets(10), -1, -1));

    JLabel label1 = new JLabel("Username:");
    usernameField = new JTextField();

    JLabel label2 = new JLabel("Host/IP:");
    hostField = new JTextField();

    JLabel label3 = new JLabel("Private Key Path:");
    keyPathField = new JTextField();

    JLabel label4 = new JLabel("Remote Path:");
    remotePathField = new JTextField();

    JLabel label5 = new JLabel("Git Branch:");
    branchField = new JTextField();

    syncButton = new JButton("Save & Sync");
    statusLabel = new JLabel("Status: Ready");

    mainPanel.add(
        label1,
        new com.intellij.uiDesigner.core.GridConstraints(
            0,
            0,
            1,
            1,
            com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST,
            com.intellij.uiDesigner.core.GridConstraints.FILL_NONE,
            com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
            com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null));
    mainPanel.add(
        usernameField,
        new com.intellij.uiDesigner.core.GridConstraints(
            0,
            1,
            1,
            1,
            com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST,
            com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL,
            com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,
            com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null));

    mainPanel.add(
        label2,
        new com.intellij.uiDesigner.core.GridConstraints(
            1,
            0,
            1,
            1,
            com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST,
            com.intellij.uiDesigner.core.GridConstraints.FILL_NONE,
            com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
            com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null));
    mainPanel.add(
        hostField,
        new com.intellij.uiDesigner.core.GridConstraints(
            1,
            1,
            1,
            1,
            com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST,
            com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL,
            com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,
            com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null));

    mainPanel.add(
        label3,
        new com.intellij.uiDesigner.core.GridConstraints(
            2,
            0,
            1,
            1,
            com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST,
            com.intellij.uiDesigner.core.GridConstraints.FILL_NONE,
            com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
            com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null));
    mainPanel.add(
        keyPathField,
        new com.intellij.uiDesigner.core.GridConstraints(
            2,
            1,
            1,
            1,
            com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST,
            com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL,
            com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,
            com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null));

    mainPanel.add(
        label4,
        new com.intellij.uiDesigner.core.GridConstraints(
            3,
            0,
            1,
            1,
            com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST,
            com.intellij.uiDesigner.core.GridConstraints.FILL_NONE,
            com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
            com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null));
    mainPanel.add(
        remotePathField,
        new com.intellij.uiDesigner.core.GridConstraints(
            3,
            1,
            1,
            1,
            com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST,
            com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL,
            com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,
            com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null));

    mainPanel.add(
        label5,
        new com.intellij.uiDesigner.core.GridConstraints(
            4,
            0,
            1,
            1,
            com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST,
            com.intellij.uiDesigner.core.GridConstraints.FILL_NONE,
            com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
            com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null));
    mainPanel.add(
        branchField,
        new com.intellij.uiDesigner.core.GridConstraints(
            4,
            1,
            1,
            1,
            com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST,
            com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL,
            com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,
            com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null));

    mainPanel.add(
        syncButton,
        new com.intellij.uiDesigner.core.GridConstraints(
            5,
            1,
            1,
            1,
            com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
            com.intellij.uiDesigner.core.GridConstraints.FILL_NONE,
            com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
            com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null));

    mainPanel.add(
        statusLabel,
        new com.intellij.uiDesigner.core.GridConstraints(
            6,
            0,
            1,
            2,
            com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST,
            com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL,
            com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,
            com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
            null,
            null,
            null));
  }
}
