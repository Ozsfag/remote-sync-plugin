package org.blacksoil.remotesync.core.ssh.ops;

import com.intellij.openapi.diagnostic.Logger;
import java.io.File;
import org.blacksoil.remotesync.infrastructure.ssh.client.SshClient;

public record RemoteFileOps(SshClient client) {
  private static final Logger LOG = Logger.getInstance(RemoteFileOps.class);

  public boolean dirExists(String path) throws Exception {
    LOG.info("→ Verifying remote path existence...");
    boolean exists = client.directoryExists(path);
    LOG.info("Path check result: " + (exists ? "EXISTS ✅" : "MISSING ❌"));
    return exists;
  }

  public void upload(File local, String target) throws Exception {
    LOG.info("→ Uploading file: " + local.getAbsolutePath() + " → " + target);
    client.uploadFile(local, target);
    LOG.info("✓ Upload completed: " + target);
  }

  public void delete(String target) throws Exception {
    LOG.info("→ Deleting file: " + target);
    client.deleteFile(target);
    LOG.info("✓ Deleted file: " + target);
  }
}
