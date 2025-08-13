package org.blacksoil.remotesync.backend.parser.config;

import javax.xml.parsers.DocumentBuilderFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class XmlConfig {
  @SneakyThrows
  @Bean
  public DocumentBuilderFactory documentBuilderFactory() {
    try {
      DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      f.setNamespaceAware(false);
      f.setXIncludeAware(false);
      f.setExpandEntityReferences(false);
      f.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      f.setFeature("http://xml.org/sax/features/external-general-entities", false);
      f.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
      try {
        f.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
      } catch (Exception ignore) {
      }
      return f;
    } catch (Exception e) {
      log.error("XmlUtils: failed to init secure XML factory, fallback: {}", e.toString());
      return DocumentBuilderFactory.newInstance();
    }
  }
}
