package org.blacksoil.sshsync.domain;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RemotePathResolver {
  public String normalize(String raw, String username) {
    if (raw == null || raw.isBlank()) return "/home/" + username + "/remote-sync";
    String path = raw.trim();
    while ((path.startsWith("\"") && path.endsWith("\""))
        || (path.startsWith("'") && path.endsWith("'"))) {
      path = path.substring(1, path.length() - 1).trim();
    }
    if (path.endsWith(".git")) path = path.substring(0, path.length() - 4);
    if ((path.startsWith("http") || path.contains("@")) && path.contains("/")) {
      path = path.substring(path.lastIndexOf('/') + 1);
    }
    if (path.startsWith("~/")) path = "/home/" + username + path.substring(1);
    if (!path.startsWith("/")) path = "/home/" + username + "/" + path;
    return path;
  }
}
