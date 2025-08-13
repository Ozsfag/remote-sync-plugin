package org.blacksoil.remotesync.backend.config;

import java.util.List;
import org.blacksoil.remotesync.backend.properties.MarketplaceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(MarketplaceProperties.class)
public class HttpClientConfig {

  @Bean
  public WebClient marketplaceWebClient(MarketplaceProperties props, WebClient.Builder builder) {
    ExchangeStrategies strategies =
        ExchangeStrategies.builder()
            .codecs(c -> c.defaultCodecs().maxInMemorySize(1_024 * 1_024)) // 1MB на XML
            .build();

    return builder
        .baseUrl(props.getBaseUrl())
        .defaultHeaders(
            h -> {
              h.set("User-Agent", props.getUserAgent());
              h.setAccept(List.of(MediaType.APPLICATION_XML));
            })
        .exchangeStrategies(strategies)
        .build();
  }
}
