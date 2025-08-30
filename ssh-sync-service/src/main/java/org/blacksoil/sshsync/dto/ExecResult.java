package org.blacksoil.sshsync.dto;

public record ExecResult(int exitCode, String stdout, String stderr) {}
