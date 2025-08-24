package org.blacksoil.remotesync.page.welcome;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.intellij.openapi.vfs.VirtualFile;
import javax.swing.*;
import org.blacksoil.remotesync.page.welcome.browserAdapter.BrowserAdapter;
import org.blacksoil.remotesync.page.welcome.htmlLoader.HtmlLoader;
import org.blacksoil.remotesync.page.welcome.provider.browser.BrowserProvider;
import org.blacksoil.remotesync.page.welcome.provider.version.PluginVersionProvider;
import org.junit.jupiter.api.Test;

class WelcomeFileEditorJcefTest {

  static class FakeBrowserAdapter implements BrowserAdapter {
    String lastHtml;
    final JComponent comp = new JPanel();

    @Override
    public JComponent getComponent() {
      return comp;
    }

    @Override
    public void loadHtml(String html) {
      this.lastHtml = html;
    }
  }

  @Test
  void createsJcefBrowserAndLoadsHtml() {
    // arrange
    var vf = mock(VirtualFile.class);

    var loader = mock(HtmlLoader.class);
    when(loader.loadOrNull("welcome/welcome.html")).thenReturn("<html>${version}</html>");
    when(loader.loadOrNull("welcome/fallback.html")).thenReturn(null);

    var version = mock(PluginVersionProvider.class);
    when(version.getVersionOrDefault()).thenReturn("1.2.3");

    var fakeBrowser = new FakeBrowserAdapter();
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
}
