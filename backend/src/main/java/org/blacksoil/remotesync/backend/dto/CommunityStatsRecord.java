package org.blacksoil.remotesync.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.With;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder(toBuilder = true)
public record CommunityStatsRecord(@With Long downloadsMonth, @With Integer sponsorsCount) {
  public static CommunityStatsRecord of(Long downloadsMonth, Integer sponsorsCount) {
    return new CommunityStatsRecord(downloadsMonth, sponsorsCount);
  }
}
