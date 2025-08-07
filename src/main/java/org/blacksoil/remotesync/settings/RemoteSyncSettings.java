package org.blacksoil.remotesync.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;

@State(
    name = "RemoteSyncSettings",
    storages = {@Storage("remote-sync-settings.xml")})
public class RemoteSyncSettings implements PersistentStateComponent<RemoteSyncSettings.State> {

  private State state = new State();

  public static RemoteSyncSettings getInstance(@NotNull Project project) {
    return project.getService(RemoteSyncSettings.class);
  }

  @Override
  public @NotNull State getState() {
    return state;
  }

  @Override
  public void loadState(@NotNull State state) {
    this.state = state;
  }

  public static class State {
    public String username = "";
    public String host = "";
    public String privateKeyPath = "";
    public String remotePath = "";
    public String branch = "";
  }

  public void applyFromUI(
      JTextField usernameField,
      JTextField hostField,
      JTextField keyPathField,
      JTextField remotePathField,
      JTextField branchField) {
    state.username = usernameField.getText().trim();
    state.host = hostField.getText().trim();
    state.privateKeyPath = keyPathField.getText().trim();
    state.remotePath = remotePathField.getText().trim();
    state.branch = branchField.getText().trim();
  }
}
