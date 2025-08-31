package org.blacksoil.sshsync.infra.factory;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.util.Objects;
import java.util.Properties;
import lombok.RequiredArgsConstructor;
import org.blacksoil.sshsync.domain.service.JschSessionFactory;
import org.blacksoil.sshsync.infra.config.SshProperties;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultJschSessionFactory implements JschSessionFactory {

  private final SshProperties props;

  @Override
  public Session open(String host, String username, String password) throws Exception {
    Objects.requireNonNull(host, "host");
    Objects.requireNonNull(username, "username");

    JSch jsch = new JSch();
    Session session = jsch.getSession(username, host, props.getPort());

    if (password != null && !password.isBlank()) {
      session.setPassword(password);
    }

    Properties config = new Properties();
    config.put("StrictHostKeyChecking", props.isStrictHostKeyChecking() ? "yes" : "no");
    config.put("PreferredAuthentications", props.getPreferredAuthentications());
    config.put("ServerAliveInterval", "10");
    session.setConfig(config);

    session.connect(props.getTimeoutMs());
    return session;
  }
}
