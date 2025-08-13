package org.blacksoil.remotesync.backend.parser.resolver;

import lombok.RequiredArgsConstructor;
import org.blacksoil.remotesync.backend.parser.util.XmlUtils;
import org.blacksoil.remotesync.backend.properties.MarketplaceProperties;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class DefaultLinkResolver implements LinkResolver {

  private final MarketplaceProperties props;

  private static String buildByIds(String base, String num, String xmlId) {
    if (hasText(xmlId)) return base + "/plugin/" + xmlId;
    if (hasText(num)) return base + "/plugin/" + num;
    return null;
  }

  private static boolean hasText(String s) {
    return s != null && !s.trim().isEmpty();
  }

  @Override
  public String resolve(String num, String xmlId, String url) {
    String byIds = buildByIds(props.getBaseUrl(), num, xmlId);
    return props.isPreferXmlIdLink() ? XmlUtils.first(byIds, url) : XmlUtils.first(url, byIds);
  }
}
