package org.blacksoil.remotesync.infrastructure.ssh.session;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.util.Objects;
import java.util.Properties;

/**
 * @param privateKeyPath может быть null
 */
public record JschSessionFactory(String privateKeyPath, boolean strictHostKeyChecking)
    implements SessionFactory {
  private static final int SSH_PORT = 22;
  private static final int TIMEOUT_MS = 15_000;

  public JschSessionFactory() {
    this(null, false);
  }

  @Override
  public Session open(String host, String username, String password) throws Exception {
    Objects.requireNonNull(host, "host");
    Objects.requireNonNull(username, "username");

    JSch jsch = new JSch();
    if (privateKeyPath != null && !privateKeyPath.isBlank()) {
      jsch.addIdentity(privateKeyPath);
    }

    Session session = jsch.getSession(username, host, SSH_PORT);
    if (password != null && !password.isBlank()) {
      session.setPassword(password);
    }

    Properties config = new Properties();
    config.put("StrictHostKeyChecking", strictHostKeyChecking ? "yes" : "no");
    session.setConfig(config);
    session.connect(TIMEOUT_MS);
    return session;
  }
}
