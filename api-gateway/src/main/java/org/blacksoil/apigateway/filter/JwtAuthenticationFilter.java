// api-gateway/src/main/java/org/blacksoil/apigateway/filter/JwtAuthenticationFilter.java
package org.blacksoil.apigateway.filter;

import lombok.RequiredArgsConstructor;
import org.blacksoil.apigateway.config.SecurityProperties;
import org.blacksoil.apigateway.util.JwtUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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

  private boolean isPermitted(String path) {
    var list = securityProperties.permittedPaths();
    return list != null && list.stream().anyMatch(p -> MATCHER.match(p, path));
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    var request = exchange.getRequest();

    if (request.getMethod() == HttpMethod.OPTIONS) {
      return chain.filter(exchange);
    }

    if (isPermitted(request.getPath().value())) {
      return chain.filter(exchange);
    }

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
