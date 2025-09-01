package org.blacksoil.sshsync.infra.factory;

import com.jcraft.jsch.Session;
import lombok.RequiredArgsConstructor;
import org.blacksoil.sshsync.app.client.DefaultSshClient;
import org.blacksoil.sshsync.app.client.SshClient;
import org.blacksoil.sshsync.domain.service.SshClientFactory;
import org.blacksoil.sshsync.domain.service.SshTransportFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultSshClientFactory implements SshClientFactory {

  private final SshTransportFactory transport;
  private final ConfigurableListableBeanFactory beanFactory;

  @Override
  public SshClient create(String host, String user, String pass) throws Exception {
    Session session = transport.open(host, user, pass);
    return beanFactory.getBean(DefaultSshClient.class, session);
  }
}
