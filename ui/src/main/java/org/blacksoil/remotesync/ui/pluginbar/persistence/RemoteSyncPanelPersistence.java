package org.blacksoil.remotesync.ui.pluginbar.persistence;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import java.util.function.Consumer;
import org.blacksoil.remotesync.ui.pluginbar.model.FormData;
import org.blacksoil.remotesync.ui.pluginbar.settings.RemoteSyncSettings;
import org.blacksoil.remotesync.ui.pluginbar.util.Debouncer;

public record RemoteSyncPanelPersistence(
    Project project, RemoteSyncSettings settings, Debouncer debouncer) {
  public RemoteSyncPanelPersistence(Project project, RemoteSyncSettings settings, int debounceMs) {
    this(project, settings, new Debouncer(debounceMs));
  }

  public void schedulePersist(FormData data) {
    debouncer.submit(() -> persist(data));
  }

  public void applyPendingAndPersist(FormData data, Consumer<String> onInfoIfPending) {
    if (debouncer.hasPending() && onInfoIfPending != null) {
      onInfoIfPending.accept("Applying pending changes…");
    }
    debouncer.flush();
    persist(data);
  }

  public void cancel() {
    debouncer.cancel();
  }

  private void persist(FormData d) {
    ApplicationManager.getApplication().executeOnPooledThread(() -> d.persist(project, settings));
  }
}
