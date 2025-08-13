package org.blacksoil.remotesync.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.With;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(toBuilder = true)
public record MarketplaceStatsRecord(
    @With String pluginId,
    @With String name,
    @With String version,
    @With String downloads,
    @With Double rating,
    @With Integer ratingCount,
    @With String lastUpdate,
    @With String link,
    @With String iconUrl) {
  public static MarketplaceStatsRecord of() {
    return new MarketplaceStatsRecord(null, null, null, null, null, null, null, null, null);
  }
}
