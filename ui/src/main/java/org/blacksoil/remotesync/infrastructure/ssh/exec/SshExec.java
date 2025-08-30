package org.blacksoil.remotesync.infrastructure.ssh.exec;

public interface SshExec {
    ExecResult run(String command, int timeoutMs) throws Exception;

    record ExecResult(int exitCode, String stdout, String stderr) {}
}
