package org.blacksoil.remotesync.backend.parser.value;

public final class MarketplaceXmlValue {
  private MarketplaceXmlValue() {}

  public enum TagName {
    PLUGIN("plugin"),
    VERSION("version");
    private final String v;

    TagName(String v) {
      this.v = v;
    }

    public String value() {
      return v;
    }
  }

  public enum AttrName {
    ID("id"),
    XML_ID("xmlId"),
    NAME("name"),
    URL("url"),
    ICON_URL("iconUrl"),
    DOWNLOADS("downloads"),
    DOWNLOADS_COUNT("downloadsCount"),
    RATING("rating"),
    VOTES("votes"),
    VERSION("version"),
    UPDATED("updated"),
    DATE("date");
    private final String v;

    AttrName(String v) {
      this.v = v;
    }

    public String value() {
      return v;
    }
  }

  // Совместимость со старым кодом:
  public static final class Tag {
    public static final String PLUGIN = TagName.PLUGIN.value();
    public static final String VERSION = TagName.VERSION.value();

    private Tag() {}
  }

  public static final class Attr {
    public static final String ID = AttrName.ID.value();
    public static final String XML_ID = AttrName.XML_ID.value();
    public static final String NAME = AttrName.NAME.value();
    public static final String URL = AttrName.URL.value();
    public static final String ICON_URL = AttrName.ICON_URL.value();
    public static final String DOWNLOADS = AttrName.DOWNLOADS.value();
    public static final String DOWNLOADS_COUNT = AttrName.DOWNLOADS_COUNT.value();
    public static final String RATING = AttrName.RATING.value();
    public static final String VOTES = AttrName.VOTES.value();
    public static final String VERSION = AttrName.VERSION.value();
    public static final String UPDATED = AttrName.UPDATED.value();
    public static final String DATE = AttrName.DATE.value();

    private Attr() {}
  }
}
