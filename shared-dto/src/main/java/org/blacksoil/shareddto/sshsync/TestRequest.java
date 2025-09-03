package org.blacksoil.shareddto.sshsync;

public record TestRequest(String host, String username, String password, String remotePath) {}
