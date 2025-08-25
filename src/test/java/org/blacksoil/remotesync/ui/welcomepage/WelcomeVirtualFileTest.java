package org.blacksoil.remotesync.ui.welcomepage;

import static org.blacksoil.remotesync.ui.welcomepage.WelcomeConstants.*;
import static org.junit.jupiter.api.Assertions.*;

import com.intellij.testFramework.LightVirtualFile;
import org.blacksoil.remotesync.ui.welcomepage.vfs.WelcomeVirtualFile;
import org.junit.jupiter.api.Test;

class WelcomeVirtualFileTest {

  @Test
  void usesProvidedContent_withoutIdeaApplication() {
    // DI: передаем готовый HTML, не трогаем classpath и IDE Application
    var vf = new WelcomeVirtualFile("<html>PRIMARY</html>");

    assertEquals("remoteSync.welcome.shown.1.2.3", WelcomeConstants.buildWelcomeShownKey("1.2.3"));
    assertFalse(vf.isWritable());
    assertInstanceOf(LightVirtualFile.class, vf);

    // читаем безопасно через getContent(), без VfsUtilCore.loadText(...)
    CharSequence content = vf.getContent();
    assertNotNull(content);
    assertTrue(content.toString().contains("PRIMARY"));
  }

  @Test
  void fallsBackToDefault_whenNullProvided() {
    // DI: передаем null -> должен использоваться DEFAULT_HTML
    var vf = new WelcomeVirtualFile((String) null);

    assertEquals("remoteSync.welcome.shown.1.2.3", WelcomeConstants.buildWelcomeShownKey("1.2.3"));
    assertFalse(vf.isWritable());

    CharSequence content = vf.getContent();
    assertNotNull(content);
    assertTrue(content.toString().contains("Unable to load welcome page"));
  }

  @Test
  void readsViaCustomReader_withFallback() {
    // Эмулируем: primary отсутствует, fallback есть
    var vf =
        new WelcomeVirtualFile(
            path -> {
              if (PRIMARY_HTML_PATH.equals(path)) return null;
              if (FALLBACK_HTML_PATH.equals(path)) return "<html>FALLBACK</html>";
              return null;
            });

    CharSequence content = vf.getContent();
    assertNotNull(content);
    assertTrue(content.toString().contains("FALLBACK"));
  }
}
