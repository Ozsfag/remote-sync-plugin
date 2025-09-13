package org.blacksoil.remotesync.ui.welcome.impl.version;

import static org.blacksoil.remotesync.ui.welcome.constant.WelcomeConstants.PLUGIN_ID;

import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.extensions.PluginId;
import org.blacksoil.remotesync.ui.welcome.api.PluginVersionProvider;

public class IntellijPluginVersionProvider implements PluginVersionProvider {

  @Override
  public String getVersionOrDefault() {
    var d = PluginManagerCore.getPlugin(PluginId.getId(PLUGIN_ID));
    return d != null ? d.getVersion() : "0.0.0";
  }
}
