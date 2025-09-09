package org.blacksoil.apigateway;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(
    properties = {
      "JWT_SECRET=test-jwt-secret-test-jwt-secret-32bytes",
      "management.endpoints.web.exposure.include=health,info,metrics,prometheus",
      "management.endpoint.health.show-details=always"
    })
class ApiGatewayIntegrationTest {

  @RegisterExtension
  @Order(1)
  static WireMockExtension gitDiffMock =
      WireMockExtension.newInstance().options(wireMockConfig().dynamicPort()).build();

  @RegisterExtension
  @Order(2)
  static WireMockExtension sshSyncMock =
      WireMockExtension.newInstance().options(wireMockConfig().dynamicPort()).build();

  @DynamicPropertySource
  static void overrideUris(DynamicPropertyRegistry registry) {
    registry.add("GIT_DIFF_URI", () -> "http://localhost:" + gitDiffMock.getPort());
    registry.add("SSH_SYNC_URI", () -> "http://localhost:" + sshSyncMock.getPort());
  }

  @LocalServerPort int port;
  private WebTestClient client;

  @BeforeEach
  void initClient() {
    this.client =
        WebTestClient.bindToServer()
            .baseUrl("http://localhost:" + port)
            .responseTimeout(Duration.ofSeconds(5))
            .build();
  }

  private static final String SECRET = "test-jwt-secret-test-jwt-secret-32bytes";

  private static String jwt() {
    long now = System.currentTimeMillis();
    SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    return Jwts.builder()
        .subject("it-user")
        .issuedAt(new Date(now))
        .expiration(new Date(now + 3600_000))
        .signWith(key) // HS256
        .compact();
  }

  @BeforeEach
  void resetStubs() {
    gitDiffMock.resetAll();
    sshSyncMock.resetAll();
  }

  @AfterEach
  void afterEach() {
    // опционально: общие проверки
  }

  // ------ PUBLIC ACTUATOR

  @Test
  void gateway_actuator_health_is_public() {
    client
        .get()
        .uri("/actuator/health")
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader()
        .contentTypeCompatibleWith("application/vnd.spring-boot.actuator.v3+json");
  }

  @Test
  void git_actuator_health_is_proxied_and_public() {
    gitDiffMock.stubFor(
        get(urlEqualTo("/actuator/health"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/vnd.spring-boot.actuator.v3+json")
                    .withBody("{\"status\":\"UP\"}")));

    client
        .get()
        .uri("/api/git/actuator/health")
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader()
        .contentTypeCompatibleWith("application/vnd.spring-boot.actuator.v3+json")
        .expectBody()
        .jsonPath("$.status")
        .isEqualTo("UP");

    gitDiffMock.verify(
        getRequestedFor(urlEqualTo("/actuator/health"))
            .withHeader("X-Gateway", equalTo("API-Gateway")));
  }

  @Test
  void ssh_actuator_health_is_proxied_and_public() {
    sshSyncMock.stubFor(
        get(urlEqualTo("/actuator/health"))
            .willReturn(
                aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/vnd.spring-boot.actuator.v3+json")
                    .withBody("{\"status\":\"UP\"}")));

    client
        .get()
        .uri("/api/ssh/actuator/health")
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader()
        .contentTypeCompatibleWith("application/vnd.spring-boot.actuator.v3+json")
        .expectBody()
        .jsonPath("$.status")
        .isEqualTo("UP");

    sshSyncMock.verify(
        getRequestedFor(urlEqualTo("/actuator/health"))
            .withHeader("X-Gateway", equalTo("API-Gateway")));
  }

  // ------ PROTECTED BUSINESS ENDPOINTS

  @Test
  void git_diff_without_jwt_is_401() {
    client
        .post()
        .uri("/api/git/diff")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue("{\"projectDir\":\"/tmp/repo\",\"branch\":\"main\"}")
        .exchange()
        .expectStatus()
        .isUnauthorized();
  }

  @Test
  void ssh_test_without_jwt_is_401() {
    client
        .post()
        .uri("/api/ssh/test")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(
            "{\"host\":\"127.0.0.1\",\"username\":\"u\",\"password\":\"p\",\"remotePath\":\"/var/www\"}")
        .exchange()
        .expectStatus()
        .isUnauthorized();
  }

  @Test
  void git_diff_with_valid_jwt_is_proxied_and_returns_200() {
    gitDiffMock.stubFor(
        post(urlEqualTo("/api/git/diff"))
            .withRequestBody(containing("\"projectDir\":\"/tmp/repo\""))
            .willReturn(okJson("{\"files\":[]}")));

    client
        .post()
        .uri("/api/git/diff")
        .header("Authorization", "Bearer " + jwt())
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue("{\"projectDir\":\"/tmp/repo\",\"branch\":\"main\"}")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.files")
        .isArray();

    gitDiffMock.verify(
        postRequestedFor(urlEqualTo("/api/git/diff"))
            .withHeader("X-Gateway", equalTo("API-Gateway")));
  }

  @Test
  void ssh_test_with_valid_jwt_is_proxied_and_returns_200() {
    sshSyncMock.stubFor(
        post(urlEqualTo("/api/ssh/test"))
            .withRequestBody(containing("\"host\":\"127.0.0.1\""))
            .willReturn(okJson("{\"ok\":true}")));

    client
        .post()
        .uri("/api/ssh/test")
        .header("Authorization", "Bearer " + jwt())
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(
            "{\"host\":\"127.0.0.1\",\"username\":\"u\",\"password\":\"p\",\"remotePath\":\"/var/www\"}")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.ok")
        .isEqualTo(true);

    sshSyncMock.verify(
        postRequestedFor(urlEqualTo("/api/ssh/test"))
            .withHeader("X-Gateway", equalTo("API-Gateway")));
  }
}
