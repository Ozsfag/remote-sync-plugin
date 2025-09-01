package org.blacksoil.sshsync.infra.factory;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.RequiredArgsConstructor;
import org.blacksoil.sshsync.domain.service.SshTransportFactory;
import org.blacksoil.sshsync.infra.config.SshProperties;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultSshTransportFactory implements SshTransportFactory {

  private final SshProperties props;

  @Override
  public Session open(String host, String user, String password) throws Exception {
    JSch jsch = new JSch();
    Session session = jsch.getSession(user, host, props != null ? props.getPort() : 22);
    session.setPassword(password);
    var config = new java.util.Properties();
    config.put("StrictHostKeyChecking", "no");
    session.setConfig(config);
    session.connect(props != null ? props.getTimeoutMs() : 15000);
    return session;
  }
}
