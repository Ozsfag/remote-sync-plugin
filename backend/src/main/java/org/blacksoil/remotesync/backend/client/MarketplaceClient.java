package org.blacksoil.remotesync.backend.client;

import lombok.RequiredArgsConstructor;
import org.blacksoil.remotesync.backend.properties.MarketplaceProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MarketplaceClient {

  private final WebClient webClient;
  private final MarketplaceProperties props;

  /** Возвращает XML описания плагина из JetBrains Marketplace. */
  public Mono<String> fetchPluginXml(String pluginId) {
    return webClient
        .get()
        .uri(uri -> uri.path("/plugins/list").queryParam("pluginId", pluginId).build())
        .retrieve()
        .bodyToMono(String.class)
        .timeout(props.getRequestTimeout());
  }
}
