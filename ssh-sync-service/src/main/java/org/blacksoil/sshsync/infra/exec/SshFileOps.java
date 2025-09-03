package org.blacksoil.sshsync.infra.exec;

import com.jcraft.jsch.Session;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.blacksoil.sshsync.domain.util.ShellEscaper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SshFileOps {
  private final SshExec exec;

  public void mkDirs(Session session, String remoteDir) throws Exception {
    if (remoteDir == null || remoteDir.isBlank() || "/".equals(remoteDir)) return;
    var r = exec.run(session, "mkdir -p " + ShellEscaper.quote(remoteDir));
    if (r.exitCode() != 0) throw new IOException("mkdir failed: " + r.stderr());
  }

  public boolean dirExists(Session session, String remoteDir) throws Exception {
    var r =
        exec.run(
            session, "[ -d " + ShellEscaper.quote(remoteDir) + " ] && echo exists || echo missing");
    return "exists".equals(r.stdout().trim());
  }

  public void rm(Session session, String remotePath) throws Exception {
    var r = exec.run(session, "rm -f " + ShellEscaper.quote(remotePath));
    if (r.exitCode() != 0) throw new IOException("rm failed: " + r.stderr());
  }
}
