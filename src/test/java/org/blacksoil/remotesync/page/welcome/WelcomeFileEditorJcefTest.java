package org.blacksoil.remotesync.page.welcome;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.intellij.openapi.vfs.VirtualFile;
import javax.swing.*;
import org.blacksoil.remotesync.page.welcome.api.Browser;
import org.blacksoil.remotesync.page.welcome.api.BrowserProvider;
import org.blacksoil.remotesync.page.welcome.api.HtmlLoader;
import org.blacksoil.remotesync.page.welcome.api.PluginVersionProvider;
import org.blacksoil.remotesync.page.welcome.ui.WelcomeFileEditor;
import org.junit.jupiter.api.Test;

class WelcomeFileEditorJcefTest {

  @Test
  void createsJcefBrowserAndLoadsHtml() {
    // arrange
    var vf = mock(VirtualFile.class);

    var loader = mock(HtmlLoader.class);
    when(loader.loadOrNull("welcome/welcome.html")).thenReturn("<html>${version}</html>");
    when(loader.loadOrNull("welcome/fallback.html")).thenReturn(null);

    var version = mock(PluginVersionProvider.class);
    when(version.getVersionOrDefault()).thenReturn("1.2.3");

    var fakeBrowser = new FakeBrowser();
    var provider = mock(BrowserProvider.class);
    when(provider.isSupported()).thenReturn(true);
    when(provider.create()).thenReturn(fakeBrowser);

    // act
    var editor = new WelcomeFileEditor(vf, provider, loader, version);

    // assert
    assertSame(fakeBrowser.comp, editor.getPreferredFocusedComponent());
    assertEquals("Welcome", editor.getName());
    assertEquals("<html>1.2.3</html>", fakeBrowser.lastHtml);
    assertNotNull(editor.getComponent());
    assertTrue(editor.isValid());
    assertFalse(editor.isModified());
  }

  static class FakeBrowser implements Browser {
    final JComponent comp = new JPanel();
    String lastHtml;

    @Override
    public JComponent getComponent() {
      return comp;
    }

    @Override
    public void loadHtml(String html) {
      this.lastHtml = html;
    }
  }
}
