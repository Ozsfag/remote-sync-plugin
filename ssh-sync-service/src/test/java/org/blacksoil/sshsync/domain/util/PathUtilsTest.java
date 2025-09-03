package org.blacksoil.sshsync.domain.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PathUtilsTest {
  @Test
  void parent_root_cases() {
    assertEquals("/", PathUtils.parent(null));
    assertEquals("/", PathUtils.parent(""));
    assertEquals("/", PathUtils.parent("/"));
    assertEquals("/", PathUtils.parent("a")); // нет слэша
  }

  @Test
  void parent_normal_cases() {
    assertEquals("/home/u", PathUtils.parent("/home/u/file.txt"));
    assertEquals("/home/u", PathUtils.parent("/home/u/"));
  }
}
