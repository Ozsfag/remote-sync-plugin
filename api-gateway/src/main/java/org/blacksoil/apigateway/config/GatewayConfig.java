package org.blacksoil.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

  @Bean
  public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
    return builder
        .routes()
        .route("git-diff-service", r -> r.path("/git-diff/**").uri("http://git-diff-service:8080"))
        .route("ssh-sync-service", r -> r.path("/ssh-sync/**").uri("http://ssh-sync-service:8080"))
        .build();
  }
}
