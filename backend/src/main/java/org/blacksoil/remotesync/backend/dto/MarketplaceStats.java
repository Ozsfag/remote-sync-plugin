package org.blacksoil.remotesync.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MarketplaceStats {
  public String pluginId;
  public String name;
  public String version;
  public String downloads; // как строку, чтобы не переполнить int и сохранить формат
  public Double rating; // 0..5
  public Integer ratingCount; // число голосов
  public String lastUpdate; // ISO8601 если удастся вытащить
  public String link; // ссылка на страницу плагина
  public String iconUrl; // если удастся вытащить

  public static MarketplaceStats of() {
    return new MarketplaceStats();
  }
}
