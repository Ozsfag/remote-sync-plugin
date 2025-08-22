package org.blacksoil.remotesync.welcome;

import com.intellij.ide.highlighter.HtmlFileType;
import com.intellij.testFramework.LightVirtualFile;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class WelcomeVirtualFile extends LightVirtualFile {
  public WelcomeVirtualFile() {
    super("Remote Sync — Welcome.html", HtmlFileType.INSTANCE, loadHtmlContent());
    setWritable(false);
  }

  private static String loadHtmlContent() {
    String content = loadFromResource("welcome/welcome.html");
    if (content == null) {
      content = loadFromResource("welcome/fallback.html");
    }
    return content != null
        ? content
        : "<html><body><h1>Unable to load welcome page</h1></body></html>";
  }

  private static String loadFromResource(String path) {
    try (InputStream is = WelcomeVirtualFile.class.getClassLoader().getResourceAsStream(path)) {
      if (is == null) return null;
      return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      return null;
    }
  }
}
