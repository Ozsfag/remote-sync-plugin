package org.blacksoil.remotesync.backend.parser.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Element;

@UtilityClass
@Slf4j
public class XmlUtils {

  public String attr(Element e, String name) {
    if (e == null || !e.hasAttribute(name)) return null;
    String v = e.getAttribute(name);
    return isEmpty(v) ? null : v.trim();
  }

  public String text(Element e) {
    if (e == null) return null;
    String v = e.getTextContent();
    return isEmpty(v) ? null : v.trim();
  }

  public boolean isEmpty(String s) {
    return s == null || s.trim().isEmpty();
  }

  /** Вернуть первый непустой. */
  public String first(String... vals) {
    if (vals == null) return null;
    for (String v : vals) if (!isEmpty(v)) return v;
    return null;
  }

  public Double toDouble(String s) {
    if (isEmpty(s)) return null;
    try {
      return Double.parseDouble(s.trim());
    } catch (Exception e) {
      return null;
    }
  }

  public Integer toInt(String s) {
    if (isEmpty(s)) return null;
    try {
      return Integer.parseInt(s.trim());
    } catch (Exception e) {
      return null;
    }
  }
}
