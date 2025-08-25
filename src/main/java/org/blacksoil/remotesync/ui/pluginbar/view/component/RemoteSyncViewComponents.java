package org.blacksoil.remotesync.ui.pluginbar.view.component;

import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import javax.swing.*;
import lombok.Builder;

/** Снимок созданных виджетов вьюхи. */
@Builder(toBuilder = true)
public record RemoteSyncViewComponents(
    JPanel root,
    JBTextField usernameField,
    JBTextField ipField,
    JBPasswordField passwordField,
    JBTextField remotePathField,
    JBTextField branchField,
    JButton testButton,
    JButton syncButton,
    JProgressBar progressBar,
    JLabel statusLabel) {}
