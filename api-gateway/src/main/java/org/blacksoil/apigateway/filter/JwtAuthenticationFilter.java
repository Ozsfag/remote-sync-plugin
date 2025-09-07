package org.blacksoil.apigateway.filter;

import lombok.RequiredArgsConstructor;
import org.blacksoil.apigateway.config.SecurityProperties;
import org.blacksoil.apigateway.util.JwtUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

  private static final AntPathMatcher MATCHER = new AntPathMatcher();

  private final JwtUtils jwtUtils;
  private final SecurityProperties securityProperties;

  private boolean isPermitted(ServerHttpRequest request) {
    String path = request.getPath().value();
    return securityProperties.permittedPaths() != null
        && securityProperties.permittedPaths().stream().anyMatch(p -> MATCHER.match(p, path));
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    var request = exchange.getRequest();

    // Разрешаем preflight CORS
    if (request.getMethod() == HttpMethod.OPTIONS) {
      return chain.filter(exchange);
    }

    // Разрешаем whitelisted пути
    if (isPermitted(request)) {
      return chain.filter(exchange);
    }

    // Требуем Bearer JWT для остальных
    String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);
      if (jwtUtils.validateToken(token)) {
        return chain.filter(exchange);
      }
    }

    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
    return exchange.getResponse().setComplete();
  }
}
