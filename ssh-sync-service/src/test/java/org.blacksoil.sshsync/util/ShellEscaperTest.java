package org.blacksoil.remotesync.infrastructure.ssh.util;

import org.junit.jupiter.api.Test;

class ShellEscaperTest {
  @Test
  void quotes_and_escapes() {
    assertEquals("\"a b\"", ShellEscaper.quote("a b"));
    assertEquals("\"a\\\"b\"", ShellEscaper.quote("a\"b"));
    assertEquals("\"a\\\\b\"", ShellEscaper.quote("a\\b"));
  }
}
