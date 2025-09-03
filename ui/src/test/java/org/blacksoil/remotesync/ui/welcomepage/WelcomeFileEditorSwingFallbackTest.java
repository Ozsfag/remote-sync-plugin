package org.blacksoil.remotesync.ui.welcomepage;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBScrollPane;
import javax.swing.*;
import org.blacksoil.remotesync.ui.welcomepage.api.BrowserProvider;
import org.blacksoil.remotesync.ui.welcomepage.api.HtmlLoader;
import org.blacksoil.remotesync.ui.welcomepage.api.PluginVersionProvider;
import org.blacksoil.remotesync.ui.welcomepage.ui.WelcomeFileEditor;
import org.junit.jupiter.api.Test;

class WelcomeFileEditorSwingFallbackTest {

  @Test
  void usesSwingFallbackWhenJcefUnsupported() {
    var vf = mock(VirtualFile.class);

    var loader = mock(HtmlLoader.class);
    when(loader.loadOrNull("welcome/welcome.html")).thenReturn(null);
    when(loader.loadOrNull("welcome/fallback.html")).thenReturn("<html>v=${version}</html>");

    var version = mock(PluginVersionProvider.class);
    when(version.getVersionOrDefault()).thenReturn("9.9.9");

    var provider = mock(BrowserProvider.class);
    when(provider.isSupported()).thenReturn(false);

    var editor = new WelcomeFileEditor(vf, provider, loader, version);

    // компонент — скролл с редактором
    JComponent focused = editor.getPreferredFocusedComponent();
    assertInstanceOf(JBScrollPane.class, focused);

    assertEquals("Welcome", editor.getName());
    assertNotNull(editor.getComponent());
    assertTrue(editor.isValid());
    assertFalse(editor.isModified());
  }
}
