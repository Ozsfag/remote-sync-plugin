package org.blacksoil.sshsync.infrastructure.ssh.client;

import com.jcraft.jsch.Session;
import java.io.File;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.blacksoil.sshsync.domain.PathUtils;
import org.blacksoil.sshsync.infra.fs.RemoteFs;
import org.blacksoil.sshsync.infra.scp.JschScpUploader;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultSshClient implements SshClient, AutoCloseable {

  private final Session session;
  private final RemoteFs fs;
  private final JschScpUploader scp;

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
