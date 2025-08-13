package org.blacksoil.remotesync.backend.parser;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import lombok.RequiredArgsConstructor;
import org.blacksoil.remotesync.backend.dto.MarketplaceStatsRecord;
import org.blacksoil.remotesync.backend.parser.resolver.LinkResolver;
import org.blacksoil.remotesync.backend.parser.util.XmlUtils;
import org.blacksoil.remotesync.backend.parser.value.MarketplaceXmlValue;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;

@Component
@RequiredArgsConstructor
public class MarketplaceParser {

  private final DocumentBuilderFactory documentBuilderFactory;
  private final LinkResolver linkResolver;

  private static boolean hasText(String s) {
    return s != null && !s.trim().isEmpty();
  }

  public MarketplaceStatsRecord parse(String xml) {
    return parse(xml, null);
  }

  public MarketplaceStatsRecord parse(String xml, String expectedIdOrXmlId) {
    MarketplaceStatsRecord out = MarketplaceStatsRecord.of();
    if (xml == null || xml.isBlank()) return out;

    try (var is = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))) {
      DocumentBuilder b = documentBuilderFactory.newDocumentBuilder();
      Document doc = b.parse(is);
      Element plugin = pickPlugin(doc, expectedIdOrXmlId);
      return plugin != null ? parsePlugin(plugin) : out;
    } catch (Exception e) {
      return out;
    }
  }

  private Element pickPlugin(Document doc, String expected) {
    NodeList list = doc.getElementsByTagName(MarketplaceXmlValue.Tag.PLUGIN);
    if (list.getLength() == 0) return null;
    if (!hasText(expected)) return (Element) list.item(0);
    for (int i = 0; i < list.getLength(); i++) {
      Element e = (Element) list.item(i);
      String id = XmlUtils.attr(e, MarketplaceXmlValue.Attr.ID);
      String xmlId = XmlUtils.attr(e, MarketplaceXmlValue.Attr.XML_ID);
      if (expected.equalsIgnoreCase(xmlId) || expected.equals(id)) return e;
    }
    return (Element) list.item(0);
  }

  private MarketplaceStatsRecord parsePlugin(Element p) {
    String name = XmlUtils.attr(p, MarketplaceXmlValue.Attr.NAME);
    String downloads =
        XmlUtils.first(
            XmlUtils.attr(p, MarketplaceXmlValue.Attr.DOWNLOADS),
            XmlUtils.attr(p, MarketplaceXmlValue.Attr.DOWNLOADS_COUNT));
    Double rating = XmlUtils.toDouble(XmlUtils.attr(p, MarketplaceXmlValue.Attr.RATING));
    Integer ratingCount = XmlUtils.toInt(XmlUtils.attr(p, MarketplaceXmlValue.Attr.VOTES));

    String id = XmlUtils.attr(p, MarketplaceXmlValue.Attr.ID);
    String xmlId = XmlUtils.attr(p, MarketplaceXmlValue.Attr.XML_ID);
    String url = XmlUtils.attr(p, MarketplaceXmlValue.Attr.URL);
    String link = linkResolver.resolve(id, xmlId, url);

    String iconUrl = XmlUtils.attr(p, MarketplaceXmlValue.Attr.ICON_URL);

    String version = null;
    String lastUpdate = null;
    String versionAttr = XmlUtils.attr(p, MarketplaceXmlValue.Attr.VERSION);
    if (hasText(versionAttr)) {
      version = versionAttr;
      lastUpdate = XmlUtils.attr(p, MarketplaceXmlValue.Attr.UPDATED);
    } else {
      NodeList versions = p.getElementsByTagName(MarketplaceXmlValue.Tag.VERSION);
      if (versions.getLength() > 0) {
        Element v = (Element) versions.item(0);
        version = XmlUtils.text(v);
        lastUpdate =
            XmlUtils.first(
                XmlUtils.attr(v, MarketplaceXmlValue.Attr.DATE),
                XmlUtils.attr(v, MarketplaceXmlValue.Attr.UPDATED),
                XmlUtils.attr(p, MarketplaceXmlValue.Attr.UPDATED));
      }
    }

    return MarketplaceStatsRecord.builder()
        .name(name)
        .downloads(downloads)
        .rating(rating)
        .ratingCount(ratingCount)
        .link(link)
        .iconUrl(iconUrl)
        .version(version)
        .lastUpdate(lastUpdate)
        .build();
  }
}
