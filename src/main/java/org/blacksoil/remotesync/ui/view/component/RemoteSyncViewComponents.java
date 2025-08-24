package org.blacksoil.remotesync.ui.view.component;

import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import javax.swing.*;

/**
 * @param root корневой контейнер с собранной формой
 * @param usernameField поля формы
 * @param testButton действия / статус
 */
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
