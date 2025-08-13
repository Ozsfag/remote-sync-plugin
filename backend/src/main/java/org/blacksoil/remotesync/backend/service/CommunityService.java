package org.blacksoil.remotesync.backend.service;

import lombok.RequiredArgsConstructor;
import org.blacksoil.remotesync.backend.dto.CommunityStatsRecord;
import org.blacksoil.remotesync.backend.dto.MarketplaceStatsRecord;
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

  public Mono<CommunityStatsRecord> summary(String pluginId) {
    String pid = CommunityUtils.normalizePluginId(pluginId);
    return marketplaceService.getStats(pid).map(ps -> toCommunity(pid, ps));
  }

  private CommunityStatsRecord toCommunity(String pluginId, MarketplaceStatsRecord ps) {
    String downloads = ps != null ? ps.downloads() : null;
    long total = CommunityUtils.parseDownloads(downloads);
    long baseline = baselineStore.getOrInitBaseline(pluginId != null ? pluginId : "unknown", total);
    long month = Math.max(0L, total - baseline);
    return CommunityStatsRecord.of(month, props.getSponsorsCount());
  }
}
