package org.blacksoil.sshsync.domain.service;

import org.blacksoil.sshsync.app.client.SshClient;

public interface SshSessionFactory {
  SshClient create(String host, String user, String password) throws Exception;
}
