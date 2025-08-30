package org.blacksoil.sshsync.infrastructure.ssh.factory;

import org.blacksoil.sshsync.infrastructure.ssh.client.SshClient;

public interface SshClientFactory {
  SshClient create(String host, String username, String password) throws Exception;
}
