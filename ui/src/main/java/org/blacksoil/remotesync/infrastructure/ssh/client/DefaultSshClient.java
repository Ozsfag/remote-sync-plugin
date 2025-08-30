package org.blacksoil.remotesync.infrastructure.ssh.client;

import com.jcraft.jsch.Session;
import java.io.File;
import java.util.Objects;
import org.blacksoil.remotesync.infrastructure.ssh.exec.JschSshExec;
import org.blacksoil.remotesync.infrastructure.ssh.exec.SshExec;
import org.blacksoil.remotesync.infrastructure.ssh.fs.DefaultRemoteFs;
import org.blacksoil.remotesync.infrastructure.ssh.fs.RemoteFs;
import org.blacksoil.remotesync.infrastructure.ssh.scp.JschScpUploader;
import org.blacksoil.remotesync.infrastructure.ssh.scp.ScpUploader;
import org.blacksoil.remotesync.infrastructure.ssh.session.JschSessionFactory;
import org.blacksoil.remotesync.infrastructure.ssh.session.SessionFactory;
import org.blacksoil.remotesync.infrastructure.ssh.util.PathUtils;

public class DefaultSshClient implements SshClient, AutoCloseable {
  private static final int TIMEOUT_MS = 15_000; // держим константу, как раньше (на будущее)

  private final Session session;
  private final RemoteFs fs;
  private final ScpUploader scp;

  /** Парольная аутентификация (как раньше). */
  public DefaultSshClient(String host, String username, String password) throws Exception {
    this(new JschSessionFactory(), host, username, password);
  }

  /** Возможность подменить фабрику (удобно в тестах/настройках). */
  public DefaultSshClient(
      SessionFactory sessionFactory, String host, String username, String password)
      throws Exception {
    Objects.requireNonNull(sessionFactory, "sessionFactory");
    this.session = sessionFactory.open(host, username, password);
    SshExec exec = new JschSshExec(session);
    this.fs = new DefaultRemoteFs(exec);
    this.scp = new JschScpUploader(session);
  }

  @Override
  public void uploadFile(File localFile, String remoteFilePath) throws Exception {
    Objects.requireNonNull(localFile, "localFile");
    Objects.requireNonNull(remoteFilePath, "remoteFilePath");

    String remoteDir = PathUtils.parent(remoteFilePath);
    fs.mkDirs(remoteDir);
    scp.upload(localFile, remoteDir, localFile.getName());
  }

  @Override
  public void deleteFile(String remoteFilePath) throws Exception {
    Objects.requireNonNull(remoteFilePath, "remoteFilePath");
    fs.rm(remoteFilePath);
  }

  @Override
  public boolean directoryExists(String remotePath) throws Exception {
    Objects.requireNonNull(remotePath, "remotePath");
    boolean exists = fs.dirExists(remotePath);

    // Сохраняем прежний «видимый» stdout (на случай отсутствия доступа к логам)
    String cmdEcho = "[ -d \"" + remotePath + "\" ] && echo exists || echo missing";
    System.out.println("Executed: " + cmdEcho + " → Response: " + (exists ? "exists" : "missing"));
    return exists;
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
