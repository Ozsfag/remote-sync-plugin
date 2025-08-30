package org.blacksoil.sshsync.infrastructure.ssh.factory;

import org.blacksoil.sshsync.infrastructure.ssh.client.DefaultSshClient;
import org.blacksoil.sshsync.infrastructure.ssh.client.SshClient;

public final class DefaultSshClientFactory implements SshClientFactory {
  @Override
  public SshClient create(String host, String username, String password) throws Exception {
    return new DefaultSshClient(host, username, password);
  }
}
