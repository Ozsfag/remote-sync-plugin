package org.blacksoil.remotesync.ui.secret;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.openapi.project.Project;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class Secrets {
  private final String SERVICE = "Remote Sync";

  private String key(Project project, String host, String user) {
    return SERVICE + "@" + project.getLocationHash() + ":" + user + "@" + host;
  }

  public void savePassword(Project project, String ip, String username, String password) {
    var attrs = new CredentialAttributes(key(project, ip, username));
    PasswordSafe.getInstance().set(attrs, new Credentials(username, String.valueOf(password)));
  }

  public @Nullable String loadPassword(Project project, String ip, String username) {
    var attrs = new CredentialAttributes(key(project, ip, username));
    var creds = PasswordSafe.getInstance().get(attrs);
    if (creds == null || creds.getPasswordAsString() == null) return null;
    return creds.getPasswordAsString();
  }
}
