package org.blacksoil.remotesync.infrastructure.ssh.fs;

public interface RemoteFs {
  void mkDirs(String dir) throws Exception;

  void rm(String path) throws Exception;

  boolean dirExists(String path) throws Exception;
}
