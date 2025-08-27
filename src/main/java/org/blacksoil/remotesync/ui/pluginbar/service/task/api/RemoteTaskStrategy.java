package org.blacksoil.remotesync.ui.pluginbar.service.task.api;

import com.intellij.openapi.project.Project;
import java.util.concurrent.atomic.AtomicBoolean;

import org.blacksoil.remotesync.ui.pluginbar.service.StatusReporter;
import org.blacksoil.remotesync.ui.pluginbar.components.persistence.RemoteSyncPanelPersistence;
import org.blacksoil.remotesync.ui.pluginbar.settings.RemoteSyncSettings;
import org.blacksoil.remotesync.ui.pluginbar.view.RemoteSyncView;
import org.blacksoil.remotesync.ui.pluginbar.view.validator.FieldsValidator;

public interface RemoteTaskStrategy {
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
