package org.blacksoil.remotesync.page.welcome.vfs;

import static org.blacksoil.remotesync.page.welcome.WelcomeConstants.*;

import com.intellij.ide.highlighter.HtmlFileType;
import com.intellij.testFramework.LightVirtualFile;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

/**
 * Виртуальный файл welcome-страницы. Имеет 3 режима: - дефолтный: читает HTML из classpath (primary
 * -> fallback -> DEFAULT_HTML) - DI с готовым HTML: принимает контент напрямую - DI с кастомным
 * ридером ресурсов (для юнит-тестов)
 */
public final class WelcomeVirtualFile extends LightVirtualFile {

  /** Прод-конструктор: читает из classpath. */
  public WelcomeVirtualFile() {
    super(
        WELCOME_FILE_NAME,
        HtmlFileType.INSTANCE,
        loadHtmlContent(WelcomeVirtualFile::loadFromResource));
    setWritable(false);
  }

  /** Тестовый/DI конструктор: задаёт HTML напрямую (null -> DEFAULT_HTML). */
  public WelcomeVirtualFile(String html) {
    super(WELCOME_FILE_NAME, HtmlFileType.INSTANCE, html != null ? html : DEFAULT_HTML);
    setWritable(false);
  }

  /** Тестовый/DI конструктор: подменяем способ чтения ресурсов. */
  public WelcomeVirtualFile(Function<String, String> resourceReader) {
    super(WELCOME_FILE_NAME, HtmlFileType.INSTANCE, loadHtmlContent(resourceReader));
    setWritable(false);
  }

  private static String loadHtmlContent(Function<String, String> reader) {
    String primary = reader.apply(PRIMARY_HTML_PATH);
    String fallback = reader.apply(FALLBACK_HTML_PATH);
    return pickHtmlOrDefault(primary, fallback);
  }

  private static String loadFromResource(String path) {
    try (InputStream is = WelcomeVirtualFile.class.getClassLoader().getResourceAsStream(path)) {
      if (is == null) return null;
      return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    } catch (Exception ignored) {
      return null;
    }
  }
}
