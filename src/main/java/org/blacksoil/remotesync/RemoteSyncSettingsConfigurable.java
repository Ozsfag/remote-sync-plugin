package org.blacksoil.remotesync;

import com.intellij.openapi.options.Configurable;
import javax.swing.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

public class RemoteSyncSettingsConfigurable implements Configurable {
  private JTextField hostField;
  private JTextField usernameField;
  private JTextField privateKeyField;
  private JTextField remotePathField;

  private final RemoteSyncSettings settings = new RemoteSyncSettings();

  @Nls(capitalization = Nls.Capitalization.Title)
  @Override
  public String getDisplayName() {
    return "Remote Sync Settings";
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    // Initialize panel and fields manually or via GUI Designer
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    hostField = new JTextField(20);
    usernameField = new JTextField(20);
    privateKeyField = new JTextField(20);
    remotePathField = new JTextField(20);

    panel.add(new JLabel("Host:"));
    panel.add(hostField);
    panel.add(new JLabel("Username:"));
    panel.add(usernameField);
    panel.add(new JLabel("Private Key Path:"));
    panel.add(privateKeyField);
    panel.add(new JLabel("Remote Path:"));
    panel.add(remotePathField);

    return panel;
  }

  @Override
  public boolean isModified() {
    RemoteSyncSettings.State state = settings.getState();
    assert state != null;
    return !hostField.getText().equals(state.host)
        || !usernameField.getText().equals(state.username)
        || !privateKeyField.getText().equals(state.privateKeyPath)
        || !remotePathField.getText().equals(state.remotePath);
  }

  @Override
  public void apply() {
    RemoteSyncSettings.State state = settings.getState();
    assert state != null;
    state.host = hostField.getText();
    state.username = usernameField.getText();
    state.privateKeyPath = privateKeyField.getText();
    state.remotePath = remotePathField.getText();
  }

  @Override
  public void reset() {
    RemoteSyncSettings.State state = settings.getState();
    assert state != null;
    hostField.setText(state.host);
    usernameField.setText(state.username);
    privateKeyField.setText(state.privateKeyPath);
    remotePathField.setText(state.remotePath);
  }
}
