package org.blacksoil.remotesync.page.welcome.provider.version;

import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.extensions.PluginId;

public class IntellijPluginVersionProvider implements PluginVersionProvider {
  private static final String PLUGIN_ID = "org.blacksoil.remotesync";

  @Override
  public String getVersionOrDefault() {
    var d = PluginManagerCore.getPlugin(PluginId.getId(PLUGIN_ID));
    return d != null ? d.getVersion() : "0.0.0";
  }
}
