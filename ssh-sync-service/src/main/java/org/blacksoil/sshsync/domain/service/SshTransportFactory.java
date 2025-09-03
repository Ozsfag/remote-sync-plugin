package org.blacksoil.sshsync.domain.service;

import com.jcraft.jsch.Session;

public interface SshTransportFactory {
  Session open(String host, String user, String pass) throws Exception;
}
