package org.blacksoil.remotesync.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.nio.file.Files;

import org.blacksoil.remotesync.backend.dto.CommunityStatsRecord;
import org.blacksoil.remotesync.backend.dto.MarketplaceStatsRecord;
import org.blacksoil.remotesync.backend.properties.CommunityProperties;
import org.blacksoil.remotesync.backend.store.BaselineStore;
import org.junit.jupiter.api.*;
import reactor.core.publisher.Mono;

class CommunityServiceTest {

  private CommunityService service;
  private MarketplaceService marketplace;

  @BeforeEach
  void setUp() throws Exception {
    File tmpDir = Files.createTempDirectory("baseline-test").toFile();
    CommunityProperties props = new CommunityProperties();
    props.setBaselineFile(new File(tmpDir, "baseline.json").getAbsolutePath());
    props.setSponsorsCount(3);

    BaselineStore baselineStore =
            new BaselineStore(props, new com.fasterxml.jackson.databind.ObjectMapper());
    marketplace = mock(MarketplaceService.class);
    service = new CommunityService(marketplace, props, baselineStore);
  }

  @Test
  void monthlyDownloads_increaseAfterSecondCall() {
    var first = MarketplaceStatsRecord.builder()
            .downloads("1000")
            .build();
    when(marketplace.getStats("org.blacksoil.remotesync")).thenReturn(Mono.just(first));

    CommunityStatsRecord c1 = service.summary("org.blacksoil.remotesync").block();
    assertNotNull(c1);
    assertEquals(0L, c1.downloadsMonth()); // baseline = 1000

    var second = MarketplaceStatsRecord.builder()
            .downloads("1300")
            .build();
    when(marketplace.getStats("org.blacksoil.remotesync")).thenReturn(Mono.just(second));

    CommunityStatsRecord c2 = service.summary("org.blacksoil.remotesync").block();
    assertNotNull(c2);
    assertEquals(300L, c2.downloadsMonth()); // 1300 - 1000
    assertEquals(3, c2.sponsorsCount());
  }
}
