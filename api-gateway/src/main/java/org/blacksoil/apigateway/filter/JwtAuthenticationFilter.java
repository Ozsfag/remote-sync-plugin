package org.blacksoil.apigateway.filter;

import org.blacksoil.apigateway.util.JwtUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements WebFilter {

  private final JwtUtils jwtUtils;

  public JwtAuthenticationFilter(JwtUtils jwtUtils) {
    this.jwtUtils = jwtUtils;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();
    String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);
      if (jwtUtils.validateToken(token)) {
        return chain.filter(exchange);
      }
    }

    exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
    return exchange.getResponse().setComplete();
  }
}
