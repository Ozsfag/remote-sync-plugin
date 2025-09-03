package org.blacksoil.sshsync.domain.service;

import org.blacksoil.sshsync.app.client.SshClient;

public interface SshClientFactory {
  SshClient create(String host, String user, String pass) throws Exception;
}
