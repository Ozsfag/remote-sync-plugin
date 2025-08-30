package org.blacksoil.remotesync.infrastructure.ssh.factory;

import org.blacksoil.remotesync.infrastructure.ssh.client.DefaultSshClient;
import org.blacksoil.remotesync.infrastructure.ssh.client.SshClient;

public final class DefaultSshClientFactory implements SshClientFactory {
  @Override
  public SshClient create(String host, String username, String password) throws Exception {
    return new DefaultSshClient(host, username, password);
  }
}
