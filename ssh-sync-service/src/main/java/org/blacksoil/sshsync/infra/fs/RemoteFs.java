package org.blacksoil.sshsync.infra.fs;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.blacksoil.sshsync.domain.ShellEscaper;
import org.blacksoil.sshsync.dto.ExecResult;
import org.blacksoil.sshsync.infra.exec.JschSshExec;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoteFs {
  private static final int TIMEOUT_MS = 15_000;
  private final JschSshExec exec;

  public void mkDirs(String dir) throws Exception {
    if (dir == null || dir.isBlank() || "/".equals(dir)) return;
    String cmd = "mkdir -p " + ShellEscaper.quote(dir);
    ExecResult r = exec.run(cmd, TIMEOUT_MS);
    if (r.exitCode() != 0) {
      throw new IOException("mkdir failed with code " + r.exitCode() + ": " + r.stderr());
    }
  }

  public void rm(String path) throws Exception {
    String cmd = "rm -f " + ShellEscaper.quote(path);
    ExecResult r = exec.run(cmd, TIMEOUT_MS);
    if (r.exitCode() != 0) {
      throw new IOException("rm failed with code " + r.exitCode() + ": " + r.stderr());
    }
  }

  public boolean dirExists(String path) throws Exception {
    String cmd = "[ -d " + ShellEscaper.quote(path) + " ] && echo exists || echo missing";
    ExecResult r = exec.run(cmd, TIMEOUT_MS);
    String out = r.stdout() == null ? "" : r.stdout().trim();
    // NB: видимый вывод в stdout, как и было в прежнем коде (делается в DefaultSshClient)
    return "exists".equals(out);
  }
}
