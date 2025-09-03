package org.blacksoil.sshsync.infra.factory;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.RequiredArgsConstructor;
import org.blacksoil.sshsync.domain.service.SshTransportFactory;
import org.blacksoil.sshsync.infra.config.SshSyncProperties;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultSshTransportFactory implements SshTransportFactory {

  private final SshSyncProperties props;

  @Override
  public Session open(String host, String user, String password) throws Exception {
    JSch jsch = new JSch();
    Session session = jsch.getSession(user, host, props.getPort());
    session.setPassword(password);
    var config = new java.util.Properties();
    config.put("StrictHostKeyChecking", "no");
    session.setConfig(config);
    session.connect(props.getTimeoutMs());
    return session;
  }
}
