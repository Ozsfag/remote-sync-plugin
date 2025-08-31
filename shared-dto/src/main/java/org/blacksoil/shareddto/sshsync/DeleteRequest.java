package org.blacksoil.shareddto.sshsync;

import java.util.List;

public record DeleteRequest(
    String host, String username, String password, String remotePath, List<String> relativePaths) {}
