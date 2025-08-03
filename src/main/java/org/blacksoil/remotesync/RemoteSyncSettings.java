package org.blacksoil.remotesync;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "RemoteSyncSettings", storages = @Storage("remoteSyncSettings.xml"))
@Service(Service.Level.PROJECT)
public final class RemoteSyncSettings
    implements PersistentStateComponent<RemoteSyncSettings.State> {

  private State state = new State();

  public static RemoteSyncSettings getInstance(Project project) {
    return project.getService(RemoteSyncSettings.class);
  }

  public static class State {
    public String username = "";
    public String host = "";
    public String privateKeyPath = "";
    public String remotePath = "";
    public String branch = "";
  }

  @Override
  public @Nullable State getState() {
    return state;
  }

  @Override
  public void loadState(@NotNull State state) {
    this.state = state;
  }
}
