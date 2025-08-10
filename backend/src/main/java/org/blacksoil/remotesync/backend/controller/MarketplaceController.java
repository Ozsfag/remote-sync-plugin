package org.blacksoil.remotesync.backend.controller;

import lombok.RequiredArgsConstructor;
import org.blacksoil.remotesync.backend.dto.PluginStats;
import org.blacksoil.remotesync.backend.service.MarketplaceService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/marketplace")
@CrossOrigin(origins = "*") // если страницы будут открываться не с того же домена
@RequiredArgsConstructor
public class MarketplaceController {

  private final MarketplaceService service;

  @GetMapping("/stats")
  public Mono<PluginStats> stats(@RequestParam String pluginId) {
    return service.getStats(pluginId);
  }
}
