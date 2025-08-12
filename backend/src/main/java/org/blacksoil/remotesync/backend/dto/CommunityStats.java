package org.blacksoil.remotesync.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommunityStats {
  public Long downloadsMonth;
  public Integer sponsorsCount;

  public static CommunityStats of(Long downloadsMonth, Integer sponsorsCount) {
    CommunityStats c = new CommunityStats();
    c.downloadsMonth = downloadsMonth;
    c.sponsorsCount = sponsorsCount;
    return c;
  }
}
