package org.blacksoil.apigateway.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = SecurityPropertiesBindingTest.Conf.class)
class SecurityPropertiesBindingTest {

  @EnableConfigurationProperties(SecurityProperties.class)
  static class Conf {}

  @Autowired SecurityProperties props;

  @Test
  void permittedPathsBoundFromYaml() {
    List<String> p = props.getPermittedPaths();
    assertThat(p).isNotEmpty();
    assertThat(p).anyMatch(s -> s.startsWith("/actuator/"));
  }
}
