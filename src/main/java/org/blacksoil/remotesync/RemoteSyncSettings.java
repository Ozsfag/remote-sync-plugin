package org.blacksoil.remotesync;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
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
}
