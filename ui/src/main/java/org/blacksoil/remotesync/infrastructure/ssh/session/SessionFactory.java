package org.blacksoil.remotesync.infrastructure.ssh.session;

import com.jcraft.jsch.Session;

public interface SessionFactory {
  Session open(String host, String username, String password) throws Exception;
}
