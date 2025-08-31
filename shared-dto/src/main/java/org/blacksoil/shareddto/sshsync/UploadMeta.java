package org.blacksoil.shareddto.sshsync;

import java.util.List;

public record UploadMeta(
    String host,
    String username,
    String password,
    String remotePath,
    String localRoot, // имя корня внутри архива (опц.)
    List<String> relativePaths) {}
