package org.blacksoil.remotesync.backend.service;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.blacksoil.remotesync.backend.dto.CommunityStats;
import org.blacksoil.remotesync.backend.dto.MarketplaceStats;
import org.blacksoil.remotesync.backend.properties.CommunityProperties;
import org.blacksoil.remotesync.backend.store.BaselineStore;
import org.blacksoil.remotesync.backend.util.CommunityUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CommunityService {

  private final MarketplaceService marketplaceService;
  private final CommunityProperties props;
  private final BaselineStore baselineStore;

  public Mono<CommunityStats> summary(String pluginId) {
    String pid = CommunityUtils.normalizePluginId(pluginId);
    return marketplaceService.getStats(pid).map(ps -> toCommunity(pid, ps));
  }

  private CommunityStats toCommunity(String pluginId, MarketplaceStats ps) {
    long total =
        CommunityUtils.parseDownloads(
            Objects.requireNonNullElse(ps, new MarketplaceStats()).downloads);
    long baseline = baselineStore.getOrInitBaseline(pluginId != null ? pluginId : "unknown", total);
    long month = Math.max(0L, total - baseline);
    return CommunityStats.of(month, props.getSponsorsCount());
  }
}
