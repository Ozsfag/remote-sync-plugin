package org.blacksoil.remotesync.backend.util;

import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class CommunityUtils {

  private final Pattern NON_DIGIT = Pattern.compile("\\D+");

  /** Убирает пробелы и проверяет на пустоту. */
  public String normalizePluginId(String pluginId) {
    if (pluginId == null) return null;
    String v = pluginId.trim();
    return v.isEmpty() ? null : v;
  }

  /**
   * Парсит строку вида "12,345" / "12 345" / "12345" в long. Если строка пустая или некорректная —
   * возвращает 0. Переполнение → Long.MAX_VALUE.
   */
  public long parseDownloads(String downloads) {
    if (downloads == null || downloads.isBlank()) return 0L;
    String digits = NON_DIGIT.matcher(downloads).replaceAll("");
    if (digits.isEmpty()) return 0L;
    try {
      return Math.max(0L, Long.parseLong(digits));
    } catch (NumberFormatException ex) {
      return Long.MAX_VALUE;
    }
  }
}
