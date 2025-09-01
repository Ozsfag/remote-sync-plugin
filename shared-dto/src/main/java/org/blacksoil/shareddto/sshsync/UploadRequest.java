package org.blacksoil.shareddto.sshsync;

import java.util.List;

public record UploadRequest(
    List<String> files,
    String host,
    String username,
    String password,
    String remotePath,
    String localRoot) {}
