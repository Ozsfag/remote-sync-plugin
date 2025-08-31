package org.blacksoil.shareddto.sshsync;

public record JobStatus(String jobId, String state, int progress, String message) {}
