package org.blacksoil.remotesync.welcome;

import com.intellij.ide.highlighter.HtmlFileType;
import com.intellij.testFramework.LightVirtualFile;

public final class WelcomeVirtualFile extends LightVirtualFile {
  public WelcomeVirtualFile() {
    super("Remote Sync — Welcome.html", HtmlFileType.INSTANCE, "");
    setWritable(false);
  }
}
