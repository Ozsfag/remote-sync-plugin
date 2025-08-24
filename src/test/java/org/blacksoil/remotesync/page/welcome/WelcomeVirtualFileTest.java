package org.blacksoil.remotesync.page.welcome;

import static org.junit.jupiter.api.Assertions.*;

import com.intellij.testFramework.LightVirtualFile;
import org.junit.jupiter.api.Test;

class WelcomeVirtualFileTest {

  @Test
  void usesProvidedContent_withoutIdeaApplication() {
    // DI-конструктор: не зависим от classpath ресурсов и IDE Application
    var vf = new WelcomeVirtualFile("<html>PRIMARY</html>");

    assertEquals(WelcomeVirtualFile.NAME, vf.getName());
    assertFalse(vf.isWritable());
    assertInstanceOf(LightVirtualFile.class, vf);

    // НЕ используем VfsUtilCore.loadText(vf) — это триггерит FileSizeLimit/Application
    CharSequence content = vf.getContent(); // безопасно в unit-тестах
    assertNotNull(content);
    assertTrue(content.toString().contains("PRIMARY"));
  }

  @Test
  void fallsBackToDefault_whenNullProvided() {
    var vf = new WelcomeVirtualFile((String) null);

    assertEquals(WelcomeVirtualFile.NAME, vf.getName());
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
              if (WelcomeVirtualFile.PRIMARY_PATH.equals(path)) return null;
              if (WelcomeVirtualFile.FALLBACK_PATH.equals(path)) return "<html>FALLBACK</html>";
              return null;
            });

    CharSequence content = vf.getContent();
    assertNotNull(content);
    assertTrue(content.toString().contains("FALLBACK"));
  }
}
