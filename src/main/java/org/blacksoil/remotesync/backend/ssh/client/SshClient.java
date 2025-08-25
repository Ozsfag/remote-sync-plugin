package org.blacksoil.remotesync.backend.ssh.client;

import java.io.File;

public interface SshClient extends AutoCloseable {
  void uploadFile(File local, String remotePath) throws Exception;

  void deleteFile(String remotePath) throws Exception;
}
