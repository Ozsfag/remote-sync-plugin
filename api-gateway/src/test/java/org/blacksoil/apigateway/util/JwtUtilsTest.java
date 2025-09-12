package org.blacksoil.apigateway.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtUtilsTest {

  private JwtUtils jwt;

  private static String token(long iat, long exp) throws Exception {
    var header =
        java.util.Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes());
    var payload =
        java.util.Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(("{\"sub\":\"u\",\"iat\":" + iat + ",\"exp\":" + exp + "}").getBytes());
    var data = header + "." + payload;
    var mac = javax.crypto.Mac.getInstance("HmacSHA256");
    mac.init(
        new javax.crypto.spec.SecretKeySpec(
            "test-secret-32bytes-minlength-!!!!".getBytes(), "HmacSHA256"));
    var sig =
        java.util.Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(mac.doFinal(data.getBytes()));
    return data + "." + sig;
  }

  @BeforeEach
  void setUp() {
    jwt = new JwtUtils();
    ReflectionTestUtils.setField(jwt, "secret", "test-secret-32bytes-minlength-!!!!");
  }

  @Test
  void validToken_returnsTrue() throws Exception {
    var now = Instant.now().getEpochSecond();
    var t = token(now, now + 3600);
    assertThat(jwt.validateToken(t)).isTrue();
  }

  @Test
  void expiredToken_returnsFalse() throws Exception {
    var now = Instant.now().getEpochSecond();
    var t = token(now - 7200, now - 3600);
    assertThat(jwt.validateToken(t)).isFalse();
  }

  @Test
  void brokenSignature_returnsFalse() throws Exception {
    var now = Instant.now().getEpochSecond();
    var t = token(now, now + 3600) + "x";
    assertThat(jwt.validateToken(t)).isFalse();
  }
}
