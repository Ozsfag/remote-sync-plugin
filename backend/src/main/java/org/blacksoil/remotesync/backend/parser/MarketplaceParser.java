package org.blacksoil.remotesync.backend.parser;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import lombok.extern.slf4j.Slf4j;
import org.blacksoil.remotesync.backend.dto.PluginStats;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;

@Component
@Slf4j
public class MarketplaceParser {

  private static final DocumentBuilderFactory XML_FACTORY = secureFactory();

  private static DocumentBuilderFactory secureFactory() {
    try {
      DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      f.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      f.setFeature("http://xml.org/sax/features/external-general-entities", false);
      f.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
      f.setExpandEntityReferences(false);
      return f;
    } catch (Exception e) {
      LoggerFactory.getLogger(MarketplaceParser.class)
          .error("Failed to init secure XML factory, fallback to default: {}", e.toString());
      return DocumentBuilderFactory.newInstance();
    }
  }

  private static String attr(Element e, String n) {
    return (e != null && e.hasAttribute(n)) ? nz(e.getAttribute(n)) : null;
  }

  private static String text(Element e) {
    String s = (e != null ? e.getTextContent() : null);
    return isEmpty(s) ? null : s.trim();
  }

  private static String first(String... v) {
    if (v != null) for (String s : v) if (!isEmpty(s)) return s;
    return null;
  }

  private static String page(String num, String xmlId) {
    if (!isEmpty(xmlId)) return "https://plugins.jetbrains.com/plugin/" + xmlId;
    if (!isEmpty(num)) return "https://plugins.jetbrains.com/plugin/" + num;
    return null;
  }

  private static boolean isEmpty(String s) {
    return s == null || s.trim().isEmpty();
  }

  private static String nz(String s) {
    return isEmpty(s) ? null : s;
  }

  private static Double toDouble(String s) {
    try {
      return isEmpty(s) ? null : Double.parseDouble(s);
    } catch (Exception e) {
      return null;
    }
  }

  private static Integer toInt(String s) {
    try {
      return isEmpty(s) ? null : Integer.parseInt(s);
    } catch (Exception e) {
      return null;
    }
  }

  public PluginStats parse(String xml) {
    PluginStats out = PluginStats.of();
    if (xml == null || xml.isBlank()) return out;

    try {
      DocumentBuilder b = XML_FACTORY.newDocumentBuilder();
      Document doc = b.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
      NodeList plugins = doc.getElementsByTagName("plugin");
      if (plugins.getLength() == 0) return out;
      return parsePlugin((Element) plugins.item(0));
    } catch (Exception e) {
      log.warn("Failed to parse Marketplace XML: {}", e.toString());
      return out;
    }
  }

  private PluginStats parsePlugin(Element p) {
    PluginStats ps = PluginStats.of();

    ps.name = attr(p, "name");
    ps.downloads = first(attr(p, "downloads"), attr(p, "downloadsCount"));
    ps.rating = toDouble(attr(p, "rating"));
    ps.ratingCount = toInt(attr(p, "votes"));
    ps.link = first(attr(p, "url"), page(attr(p, "id"), attr(p, "xmlId")));
    ps.iconUrl = attr(p, "iconUrl");

    String versionAttr = attr(p, "version");
    if (isEmpty(versionAttr)) {
      NodeList versions = p.getElementsByTagName("version");
      if (versions.getLength() > 0) {
        Element v = (Element) versions.item(0);
        ps.version = text(v);
        ps.lastUpdate = first(attr(v, "date"), attr(v, "updated"), attr(p, "updated"));
      }
    } else {
      ps.version = versionAttr;
      ps.lastUpdate = attr(p, "updated");
    }
    return ps;
  }
}
