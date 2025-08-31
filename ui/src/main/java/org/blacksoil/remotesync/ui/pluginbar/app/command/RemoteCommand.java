package org.blacksoil.remotesync.ui.pluginbar.app.command;

import com.intellij.openapi.project.Project;
import java.util.concurrent.atomic.AtomicBoolean;

import org.blacksoil.remotesync.ui.pluginbar.app.TaskCallback;
import org.blacksoil.remotesync.ui.pluginbar.presentation.status.StatusReporter;
import org.blacksoil.remotesync.ui.pluginbar.persistence.RemoteSyncPanelPersistence;
import org.blacksoil.remotesync.ui.pluginbar.settings.RemoteSyncSettings;
import org.blacksoil.remotesync.ui.pluginbar.presentation.view.RemoteSyncView;
import org.blacksoil.remotesync.ui.pluginbar.presentation.validation.FieldsValidator;

public interface RemoteCommand {
    String title();

    boolean prepare(Project project,
                    RemoteSyncSettings settings,
                    FieldsValidator validator,
                    RemoteSyncView view,
                    AtomicBoolean running,
                    RemoteSyncPanelPersistence persistence,
                    StatusReporter status);

    void execute(Project project, RemoteSyncSettings.State state, TaskCallback callback);

    TaskCallback uiCallback(StatusReporter status);

    void finish(RemoteSyncView view, AtomicBoolean running);
}
