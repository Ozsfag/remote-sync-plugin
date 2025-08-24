package org.blacksoil.remotesync.page.welcome;

import com.intellij.ide.highlighter.HtmlFileType;
import com.intellij.testFramework.LightVirtualFile;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

public final class WelcomeVirtualFile extends LightVirtualFile {
  public static final String NAME = "Remote Sync — Welcome.html";
  public static final String PRIMARY_PATH = "welcome/welcome.html";
  public static final String FALLBACK_PATH = "welcome/fallback.html";
  public static final String DEFAULT_HTML =
      "<html><body><h1>Unable to load welcome page</h1></body></html>";

  /** Прод-конструктор: читает из classpath. */
  public WelcomeVirtualFile() {
    super(NAME, HtmlFileType.INSTANCE, loadHtmlContent());
    setWritable(false);
  }

  /** Тестовый/DI конструктор: контент задаётся напрямую. */
  public WelcomeVirtualFile(String html) {
    super(NAME, HtmlFileType.INSTANCE, html != null ? html : DEFAULT_HTML);
    setWritable(false);
  }

  /** Тестовый/DI конструктор: можно подменить загрузчик ресурсов. */
  public WelcomeVirtualFile(Function<String, String> resourceReader) {
    super(NAME, HtmlFileType.INSTANCE, loadHtmlContent(resourceReader));
    setWritable(false);
  }

  private static String loadHtmlContent() {
    return loadHtmlContent(WelcomeVirtualFile::loadFromResource);
  }

  private static String loadHtmlContent(Function<String, String> reader) {
    String content = reader.apply(PRIMARY_PATH);
    if (content == null) content = reader.apply(FALLBACK_PATH);
    return content != null ? content : DEFAULT_HTML;
  }

  private static String loadFromResource(String path) {
    try (InputStream is = WelcomeVirtualFile.class.getClassLoader().getResourceAsStream(path)) {
      if (is == null) return null;
      return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    } catch (Exception ignored) { // IOException/Unchecked
      return null;
    }
  }
}
