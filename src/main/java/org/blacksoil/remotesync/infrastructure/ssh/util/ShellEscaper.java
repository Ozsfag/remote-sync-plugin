package org.blacksoil.remotesync.infrastructure.ssh.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class ShellEscaper {

  /** Примитивная безопасная кавычка для sh: "…", экранируем \ и " */
  public String quote(String s) {
    String p = s.replace("\\", "\\\\").replace("\"", "\\\"");
    return "\"" + p + "\"";
  }
}
