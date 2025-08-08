package org.blacksoil.remotesync.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.util.ui.JBUI;
import java.awt.*;
import javax.swing.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

public class RemoteSyncSettingsConfigurable implements Configurable {
  private JTextField hostField;
  private JTextField usernameField;
  private JTextField privateKeyField;
  private JTextField remotePathField;

  private final RemoteSyncSettings settings = new RemoteSyncSettings();
  private JPanel mainPanel;

  @Nls(capitalization = Nls.Capitalization.Title)
  @Override
  public String getDisplayName() {
    return "Remote Sync Settings";
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    mainPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = JBUI.insets(4);
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;

    hostField = createLabeledField("Host:", 0, gbc);
    usernameField = createLabeledField("Username:", 1, gbc);
    privateKeyField = createLabeledField("Private Key Path:", 2, gbc);
    remotePathField = createLabeledField("Remote Path:", 3, gbc);

    return mainPanel;
  }

  private JTextField createLabeledField(String label, int y, GridBagConstraints gbc) {
    gbc.gridy = y;
    gbc.gridx = 0;
    mainPanel.add(new JLabel(label), gbc);

    gbc.gridx = 1;
    JTextField field = new JTextField(20);
    mainPanel.add(field, gbc);

    return field;
  }

  @Override
  public boolean isModified() {
    RemoteSyncSettings.State state = settings.getState();
    return !hostField.getText().equals(state.host)
        || !usernameField.getText().equals(state.username)
        || !privateKeyField.getText().equals(state.privateKeyPath)
        || !remotePathField.getText().equals(state.remotePath);
  }

  @Override
  public void apply() {
    RemoteSyncSettings.State state = settings.getState();
    state.host = hostField.getText();
    state.username = usernameField.getText();
    state.privateKeyPath = privateKeyField.getText();
    state.remotePath = remotePathField.getText();
  }

  @Override
  public void reset() {
    RemoteSyncSettings.State state = settings.getState();
    hostField.setText(state.host);
    usernameField.setText(state.username);
    privateKeyField.setText(state.privateKeyPath);
    remotePathField.setText(state.remotePath);
  }
}
