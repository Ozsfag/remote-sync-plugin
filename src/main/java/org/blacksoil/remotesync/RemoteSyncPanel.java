package org.blacksoil.remotesync;

import static org.blacksoil.remotesync.util.UIUtils.addLabeledTextField;
import static org.blacksoil.remotesync.util.UIUtils.constraints;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.ui.JBUI;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Objects;
import javax.swing.*;
import org.blacksoil.remotesync.secret.Secrets;
import org.blacksoil.remotesync.service.RemoteSyncService;
import org.blacksoil.remotesync.settings.RemoteSyncSettings;
import org.blacksoil.remotesync.validator.PanelValidator;
import org.jetbrains.annotations.NotNull;

public class RemoteSyncPanel {
  private JPanel mainPanel;
  private JTextField usernameField;
  private JTextField ipField;
  private JTextField passwordField;
  private JTextField remotePathField;
  private JTextField branchField;
  private JButton syncButton;
  private JLabel statusLabel;
  private JProgressBar progressBar;

  public RemoteSyncPanel(Project project) {
    initUI();
    applyLookAndFeel();
    setToolTips();

    RemoteSyncSettings settings = RemoteSyncSettings.getInstance(project);
    RemoteSyncSettings.State state = Objects.requireNonNull(settings.getState());

    Secrets.savePassword(project, state.ip, state.username, passwordField.getText());
    writeForm(state, project);
    setupSyncAction(project, settings);
  }

  private void initUI() {
    mainPanel = new JPanel(new GridLayoutManager(8, 2, JBUI.insets(10), -1, -1));

    usernameField = addLabeledTextField(mainPanel, 0, "Username:");
    ipField = addLabeledTextField(mainPanel, 1, "IP:");
    passwordField = addLabeledTextField(mainPanel, 2, "Password:");
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

  private void setupEnterKeyShortcut() {
    ActionListener enterListener =
        e -> {
          if (syncButton.isEnabled()) syncButton.doClick();
        };

    usernameField.addActionListener(enterListener);
    ipField.addActionListener(enterListener);
    passwordField.addActionListener(enterListener);
    remotePathField.addActionListener(enterListener);
    branchField.addActionListener(enterListener);
  }

  private void applyLookAndFeel() {
    try {
      UIManager.setLookAndFeel(UIManager.getLookAndFeel());
      SwingUtilities.updateComponentTreeUI(mainPanel);
    } catch (Exception ignored) {
    }
  }

  private void setToolTips() {
    usernameField.setToolTipText("Username for SSH login");
    ipField.setToolTipText("Remote server IP or hostname");
    passwordField.setToolTipText("Path to your private SSH key");
    remotePathField.setToolTipText("Remote directory where files will be synced");
    branchField.setToolTipText("Git branch to compare changes against (e.g., main)");
  }

  private void writeForm(RemoteSyncSettings.State state, Project project) {
    usernameField.setText(state.username);
    ipField.setText(state.ip);
    passwordField.setText(Secrets.loadPassword(project, state.ip, state.username));
    remotePathField.setText(state.remotePath);
    branchField.setText(state.branch != null ? state.branch : "main");
  }

  private void setupSyncAction(Project project, RemoteSyncSettings settings) {
    syncButton.addActionListener(
        e -> {
          updateStatus("Saving settings...");
          setUiEnabled(false);

          if (!PanelValidator.isValid(
              usernameField, ipField, passwordField, remotePathField, branchField)) {
            updateStatus("Please fill in all required fields.");
            setUiEnabled(true);
            return;
          }

          persist(project, settings);
          setCursorWait(true);

          new Task.Backgroundable(project, "Remote sync", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
              RemoteSyncService.sync(
                  project,
                  settings.getState(),
                  new RemoteSyncService.SyncCallback() {
                    @Override
                    public void onStatus(String msg) {
                      indicator.setText(msg);
                      updateStatus(msg);
                    }

                    @Override
                    public void onError(String err) {
                      updateStatus(err);
                    }

                    @Override
                    public void onComplete() {
                      updateStatus("Sync complete.");
                    }
                  });
            }

            @Override
            public void onFinished() {
              setUiEnabled(true);
              setCursorWait(false);
            }
          }.queue();
        });
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

  private void persist(Project project, RemoteSyncSettings settings) {
    RemoteSyncSettings.State newState = readForm();
    settings.loadState(newState);
    String pwd = passwordField.getText();
    Secrets.savePassword(project, newState.ip, newState.username, pwd);
  }

  private RemoteSyncSettings.State readForm() {
    RemoteSyncSettings.State s = new RemoteSyncSettings.State();
    s.username = usernameField.getText().trim();
    s.ip = ipField.getText().trim();
    s.remotePath = remotePathField.getText().trim();
    s.branch = branchField.getText().trim();
    return s;
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
