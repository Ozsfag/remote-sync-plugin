package org.blacksoil.remotesync.infrastructure.ssh.fs;

import java.io.IOException;
import java.util.Objects;
import org.blacksoil.remotesync.infrastructure.ssh.exec.SshExec;
import org.blacksoil.remotesync.infrastructure.ssh.util.ShellEscaper;

public record DefaultRemoteFs(SshExec exec) implements RemoteFs {
  private static final int TIMEOUT_MS = 15_000;

  public DefaultRemoteFs(SshExec exec) {
    this.exec = Objects.requireNonNull(exec, "exec");
  }

  @Override
  public void mkDirs(String dir) throws Exception {
    if (dir == null || dir.isBlank() || "/".equals(dir)) return;
    String cmd = "mkdir -p " + ShellEscaper.quote(dir);
    SshExec.ExecResult r = exec.run(cmd, TIMEOUT_MS);
    if (r.exitCode() != 0) {
      throw new IOException("mkdir failed with code " + r.exitCode() + ": " + r.stderr());
    }
  }

  @Override
  public void rm(String path) throws Exception {
    String cmd = "rm -f " + ShellEscaper.quote(path);
    SshExec.ExecResult r = exec.run(cmd, TIMEOUT_MS);
    if (r.exitCode() != 0) {
      throw new IOException("rm failed with code " + r.exitCode() + ": " + r.stderr());
    }
  }

  @Override
  public boolean dirExists(String path) throws Exception {
    String cmd = "[ -d " + ShellEscaper.quote(path) + " ] && echo exists || echo missing";
    SshExec.ExecResult r = exec.run(cmd, TIMEOUT_MS);
    String out = r.stdout() == null ? "" : r.stdout().trim();
    // NB: видимый вывод в stdout, как и было в прежнем коде (делается в DefaultSshClient)
    return "exists".equals(out);
  }
}
