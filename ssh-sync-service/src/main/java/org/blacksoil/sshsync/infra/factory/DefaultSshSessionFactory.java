package org.blacksoil.sshsync.infra.factory;

import com.jcraft.jsch.Session;
import lombok.RequiredArgsConstructor;
import org.blacksoil.sshsync.app.client.DefaultSshClient;
import org.blacksoil.sshsync.app.client.SshClient;
import org.blacksoil.sshsync.domain.service.JschSessionFactory;
import org.blacksoil.sshsync.domain.service.SshSessionFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultSshSessionFactory implements SshSessionFactory {
  private final JschSessionFactory defaultJschSessionFactory;

  private final ObjectProvider<DefaultSshClient> clientProv; // прототип клиента

  @Override
  public SshClient create(String host, String user, String password) throws Exception {
    Session session = defaultJschSessionFactory.open(host, user, password);
    // создаём прототип клиента, передаём session в его конструктор
    return clientProv.getObject(session);
  }
}
