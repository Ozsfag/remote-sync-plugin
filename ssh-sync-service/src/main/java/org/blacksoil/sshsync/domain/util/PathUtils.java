package org.blacksoil.sshsync.domain.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PathUtils {

  public String parent(String path) {
    if (path == null || path.isEmpty()) return "/";
    int i = path.lastIndexOf('/');
    return i <= 0 ? "/" : path.substring(0, i);
  }
}
