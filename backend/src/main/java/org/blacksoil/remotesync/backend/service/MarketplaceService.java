package org.blacksoil.remotesync.backend.service;

import com.github.benmanes.caffeine.cache.Cache;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.blacksoil.remotesync.backend.client.MarketplaceClient;
import org.blacksoil.remotesync.backend.dto.PluginStats;
import org.blacksoil.remotesync.backend.parser.MarketplaceParser;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarketplaceService {

  private static final Pattern PLUGIN_ID_OK = Pattern.compile("^[\\w.\\-]{1,200}$");

  private final MarketplaceClient client; // ходит в Marketplace
  private final MarketplaceParser parser; // парсит XML -> DTO
  private final Cache<String, PluginStats> cache; // Caffeine

  private static String normalize(String pluginId) {
    if (pluginId == null) return null;
    String v = pluginId.trim();
    return PLUGIN_ID_OK.matcher(v).matches() ? v : null;
  }

  public Mono<PluginStats> getStats(String pluginId) {
    String pid = normalize(pluginId);
    if (pid == null) return Mono.just(PluginStats.of());

    // cache hit
    PluginStats cached = cache.getIfPresent(pid);
    if (cached != null) {
      return Mono.just(cached);
    }

    // сеть -> парсинг -> кэш
    return client
        .fetchPluginXml(pid)
        .map(parser::parse)
        .map(
            ps -> {
              ps.pluginId = pid;
              return ps;
            })
        .doOnNext(ps -> cache.put(pid, ps))
        .onErrorResume(
            e -> {
              log.warn("Marketplace request failed for pluginId={}: {}", pid, e.getMessage());
              return Mono.just(PluginStats.of());
            });
  }
}
