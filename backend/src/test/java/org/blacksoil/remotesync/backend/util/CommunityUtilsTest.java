package org.blacksoil.remotesync.backend.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CommunityUtilsTest {

  @Test
  void normalize_ok() {
    assertEquals(
        "org.blacksoil.remotesync",
        CommunityUtils.normalizePluginId("  org.blacksoil.remotesync "));
    assertNull(CommunityUtils.normalizePluginId("   "));
    assertNull(CommunityUtils.normalizePluginId(null));
  }

  @Test
  void parseDownloads_formats() {
    assertEquals(12345L, CommunityUtils.parseDownloads("12,345"));
    assertEquals(12345L, CommunityUtils.parseDownloads("12 345"));
    assertEquals(12345L, CommunityUtils.parseDownloads("12_345"));
    assertEquals(12345L, CommunityUtils.parseDownloads("12345"));
    assertEquals(0L, CommunityUtils.parseDownloads("abc"));
    assertEquals(0L, CommunityUtils.parseDownloads(null));
  }
}
