package org.blacksoil.remotesync.backend.store;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.nio.file.Files;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.blacksoil.remotesync.backend.properties.CommunityProperties;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BaselineStore {

  private final CommunityProperties props;
  private final ObjectMapper objectMapper;

  // pluginId -> (YYYY-MM -> baseline)
  private Map<String, Map<String, Long>> cache;

  private File file() {
    return new File(props.getBaselineFile());
  }

  private void ensureLoaded() {
    if (cache != null) return;
    try {
      File f = file();
      if (!f.exists()) {
        if (f.getParentFile() != null) f.getParentFile().mkdirs();
        cache = new HashMap<>();
        return;
      }
      byte[] bytes = Files.readAllBytes(f.toPath());
      if (bytes.length == 0) {
        cache = new HashMap<>();
      } else {
        cache = objectMapper.readValue(bytes, new TypeReference<>() {});
        if (cache == null) cache = new HashMap<>();
      }
    } catch (Exception e) {
      cache = new HashMap<>();
    }
  }

  private void persist() {
    try {
      File f = file();
      if (f.getParentFile() != null) f.getParentFile().mkdirs();
      byte[] json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(cache);
      Files.write(f.toPath(), json);
    } catch (Exception ignored) {
    }
  }

  /**
   * Возвращает baseline для (pluginId, текущий месяц). Если нет — записывает currentTotal как
   * baseline и его же возвращает.
   */
  public synchronized long getOrInitBaseline(String pluginId, long currentTotal) {
    if (pluginId == null || pluginId.isBlank()) return currentTotal;
    ensureLoaded();

    String ym = YearMonth.now().toString(); // YYYY-MM
    Map<String, Long> byPlugin = cache.computeIfAbsent(pluginId, k -> new HashMap<>());
    Long existing = byPlugin.get(ym);
    if (existing != null) return existing;

    byPlugin.put(ym, currentTotal);
    persist();
    return currentTotal;
  }
}
