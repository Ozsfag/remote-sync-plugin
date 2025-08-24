package org.blacksoil.remotesync.page.welcome.impl.resource;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.blacksoil.remotesync.page.welcome.api.HtmlLoader;
import org.blacksoil.remotesync.page.welcome.ui.WelcomeFileEditor;

public class ClasspathHtmlLoader implements HtmlLoader {
  @Override
  public String loadOrNull(String resourcePath) {
    try (InputStream is =
        WelcomeFileEditor.class.getClassLoader().getResourceAsStream(resourcePath)) {
      if (is == null) return null;
      return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    } catch (Exception ignored) {
      return null;
    }
  }
}
