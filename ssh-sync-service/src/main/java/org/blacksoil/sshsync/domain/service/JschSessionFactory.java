package org.blacksoil.sshsync.domain.service;

import com.jcraft.jsch.Session;

public interface JschSessionFactory {
    Session open(String host, String username, String password) throws Exception;
}
