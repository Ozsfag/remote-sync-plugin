package org.blacksoil.sshsync.domain;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ShellEscaper {

  public String quote(String s) {
    String p = s.replace("\\", "\\\\").replace("\"", "\\\"");
    return "\"" + p + "\"";
  }
}
