package org.blacksoil.sshsync.domain.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PathUtils {

  public String parent(String path) {
    if (path == null || path.isEmpty()) return "/";
    int i = path.lastIndexOf('/');
    return i <= 0 ? "/" : path.substring(0, i);
  }

  public String name(String path) {
    int lastSlash = path.lastIndexOf('/');
    return (lastSlash >= 0) ? path.substring(lastSlash + 1) : path;
  }
}
