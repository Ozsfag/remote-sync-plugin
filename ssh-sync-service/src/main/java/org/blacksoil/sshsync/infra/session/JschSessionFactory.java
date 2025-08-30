package org.blacksoil.sshsync.infra.session;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.util.Objects;
import java.util.Properties;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JschSessionFactory {
  private static final int SSH_PORT = 22;
  private static final int TIMEOUT_MS = 15_000;

  public Session open(String host, String username, String password) throws Exception {
    Objects.requireNonNull(host, "host");
    Objects.requireNonNull(username, "username");

    JSch jsch = new JSch();

    Session session = jsch.getSession(username, host, SSH_PORT);
    if (password != null && !password.isBlank()) {
      session.setPassword(password);
    }

    Properties config = new Properties();
    session.setConfig(config);
    session.connect(TIMEOUT_MS);
    return session;
  }
}
