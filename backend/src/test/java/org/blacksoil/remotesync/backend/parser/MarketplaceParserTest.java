package org.blacksoil.remotesync.backend.parser;

import static org.junit.jupiter.api.Assertions.*;

import org.blacksoil.remotesync.backend.dto.MarketplaceStats;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = {MarketplaceParser.class})
@TestPropertySource(properties = {"remote-sync.marketplace.prefer-xml-id-link=true"})
class MarketplaceParserTest {

  @Autowired private MarketplaceParser parser;

  @Test
  void parse_basic_prefersXmlIdForLink() {
    String xml =
        """
      <plugins>
        <plugin id="12345"
                xmlId="org.blacksoil.remotesync"
                name="Remote Sync"
                downloads="67890"
                rating="4.7"
                votes="12"
                url="https://plugins.jetbrains.com/plugin/12345"
                iconUrl="https://plugins.jetbrains.com/files/12345/icon.png"
                version="1.2.3"
                updated="2025-08-01"/>
      </plugins>
      """;

    MarketplaceStats ps = parser.parse(xml);

    assertEquals("Remote Sync", ps.name);
    assertEquals("1.2.3", ps.version);
    assertEquals("67890", ps.downloads);
    assertEquals(4.7, ps.rating);
    assertEquals(12, ps.ratingCount);
    // Приоритет по проперти: xmlId → numeric → url
    assertEquals("https://plugins.jetbrains.com/plugin/org.blacksoil.remotesync", ps.link);
    assertEquals("https://plugins.jetbrains.com/files/12345/icon.png", ps.iconUrl);
    assertEquals("2025-08-01", ps.lastUpdate);
  }

  @Test
  void parse_fallbackToNumericIdWhenNoXmlId() {
    String xml =
        """
      <plugins>
        <plugin id="98765"
                name="Remote Sync"
                downloadsCount="100500"
                rating="4.2"
                votes="7"
                version="0.9.0"/>
      </plugins>
      """;

    MarketplaceStats ps = parser.parse(xml);

    assertEquals("Remote Sync", ps.name);
    assertEquals("0.9.0", ps.version);
    assertEquals("100500", ps.downloads);
    // xmlId отсутствует → используем numeric id
    assertEquals("https://plugins.jetbrains.com/plugin/98765", ps.link);
  }

  @Test
  void parse_versionFromNestedElementAndDateFallback() {
    String xml =
        """
      <plugins>
        <plugin id="111" xmlId="com.example.plugin" name="Example">
          <version date="2025-07-15">2.0.1</version>
        </plugin>
      </plugins>
      """;

    MarketplaceStats ps = parser.parse(xml);

    assertEquals("Example", ps.name);
    assertEquals("2.0.1", ps.version);
    assertEquals("2025-07-15", ps.lastUpdate);
    assertEquals("https://plugins.jetbrains.com/plugin/com.example.plugin", ps.link);
  }

  @Test
  void parse_fallbackToUrlWhenNoIds() {
    String xml =
        """
      <plugins>
        <plugin name="No Ids"
                url="https://plugins.jetbrains.com/plugin/some-page"
                version="1.0.0"/>
      </plugins>
      """;

    MarketplaceStats ps = parser.parse(xml);

    assertEquals("No Ids", ps.name);
    assertEquals("1.0.0", ps.version);
    assertEquals("https://plugins.jetbrains.com/plugin/some-page", ps.link);
  }

  @Test
  void parse_emptyInputReturnsEmptyDto() {
    MarketplaceStats ps = parser.parse("");
    assertNull(ps.name);
    assertNull(ps.version);
    assertNull(ps.downloads);
    assertNull(ps.link);
  }
}
