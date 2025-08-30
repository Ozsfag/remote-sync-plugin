package org.blacksoil.remotesync.infrastructure.ssh.factory;

import org.blacksoil.remotesync.infrastructure.ssh.client.SshClient;

public interface SshClientFactory {
  SshClient create(String host, String username, String password) throws Exception;
}
