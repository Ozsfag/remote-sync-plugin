package org.blacksoil.remotesync.backend.controller;

import lombok.RequiredArgsConstructor;
import org.blacksoil.remotesync.backend.dto.CommunityStats;
import org.blacksoil.remotesync.backend.service.CommunityService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/community")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CommunityController {
  private final CommunityService service;

  @GetMapping("/summary")
  public Mono<CommunityStats> summary(@RequestParam String pluginId) {
    return service.summary(pluginId);
  }
}
