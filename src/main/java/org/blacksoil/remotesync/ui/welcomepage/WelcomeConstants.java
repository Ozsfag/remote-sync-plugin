package org.blacksoil.remotesync.ui.welcomepage;

import lombok.experimental.UtilityClass;

@UtilityClass
public class WelcomeConstants {

  /** Имя виртуального файла в IDE. */
  public String WELCOME_FILE_NAME = "Remote Sync — Welcome.html";

  /** Путь к основному HTML в classpath (src/main/resources). */
  public String PRIMARY_HTML_PATH = "welcome/welcome.html";

  /** Путь к fallback HTML в classpath (src/main/resources). */
  public String FALLBACK_HTML_PATH = "welcome/fallback.html";

  /** Плейсхолдер версии, который подменяем в HTML. */
  public String HTML_VERSION_PLACEHOLDER = "${version}";

  /** Текст по умолчанию, если оба ресурса недоступны/непрочитаны. */
  public String DEFAULT_HTML = "<html><body><h1>Unable to load welcome page</h1></body></html>";

  /** ID плагина (должен совпадать с plugin.xml). */
  public String PLUGIN_ID = "org.blacksoil.remotesync";

  /** Префикс ключа в PropertiesComponent: "показали welcome для версии X". */
  public String WELCOME_SHOWN_KEY_PREFIX = "remoteSync.welcome.shown.";

  /** Формирует ключ вида {@code remoteSync.welcome.shown.<version>}. */
  public String buildWelcomeShownKey(String version) {
    return WELCOME_SHOWN_KEY_PREFIX + version;
  }

  /**
   * Подставляет версию в HTML по плейсхолдеру {@link #HTML_VERSION_PLACEHOLDER}. Если html == null,
   * вернёт null (ничего не трогаем).
   */
  public String applyVersion(String html, String version) {
    if (html == null) return null;
    return html.replace(HTML_VERSION_PLACEHOLDER, version == null ? "0.0.0" : version);
  }

  /** Возвращает primary, если он не null, иначе fallback; если оба null — {@link #DEFAULT_HTML}. */
  public String pickHtmlOrDefault(String primary, String fallback) {
    if (primary != null) return primary;
    if (fallback != null) return fallback;
    return DEFAULT_HTML;
  }
}
