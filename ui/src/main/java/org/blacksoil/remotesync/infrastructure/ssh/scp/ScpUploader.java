package org.blacksoil.remotesync.infrastructure.ssh.scp;

import java.io.File;

public interface ScpUploader {
  void upload(File localFile, String remoteDir, String remoteFileName) throws Exception;
}
