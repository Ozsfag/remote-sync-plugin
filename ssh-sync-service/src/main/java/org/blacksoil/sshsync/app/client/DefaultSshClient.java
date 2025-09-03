package org.blacksoil.sshsync.app.client;

import com.jcraft.jsch.Session;
import java.io.File;
import org.blacksoil.sshsync.domain.util.PathUtils;
import org.blacksoil.sshsync.infra.exec.SshFileOps;
import org.blacksoil.sshsync.infra.scp.JschScpUploader;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.cglib.core.internal.Function;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DefaultSshClient implements SshClient, AutoCloseable {

  private final Session session;
  private final SshFileOps fileOps;
  private final Function<Session, JschScpUploader> uploaderFactory;

  public DefaultSshClient(
      Session session, SshFileOps fileOps, Function<Session, JschScpUploader> uploaderFactory) {
    this.session = session;
    this.fileOps = fileOps;
    this.uploaderFactory = uploaderFactory;
  }

  @Override
  public void uploadFile(File localFile, String remoteFilePath) throws Exception {
    String remoteDir = PathUtils.parent(remoteFilePath);
    String remoteName = PathUtils.name(remoteFilePath);
    fileOps.mkDirs(session, remoteDir);
    uploaderFactory.apply(session).upload(localFile, remoteDir, remoteName);
  }

  @Override
  public void deleteFile(String remoteFilePath) throws Exception {
    fileOps.rm(session, remoteFilePath);
  }

  @Override
  public boolean directoryExists(String remotePath) throws Exception {
    return fileOps.dirExists(session, remotePath);
  }

  @Override
  public void close() {
    if (session != null && session.isConnected()) {
      try {
        session.disconnect();
      } catch (Throwable ignored) {
      }
    }
  }
}
