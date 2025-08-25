package org.blacksoil.remotesync.ui.welcomepage.impl.version;

import static org.blacksoil.remotesync.ui.welcomepage.WelcomeConstants.*;

import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.extensions.PluginId;
import org.blacksoil.remotesync.ui.welcomepage.api.PluginVersionProvider;

public class IntellijPluginVersionProvider implements PluginVersionProvider {

  @Override
  public String getVersionOrDefault() {
    var d = PluginManagerCore.getPlugin(PluginId.getId(PLUGIN_ID));
    return d != null ? d.getVersion() : "0.0.0";
  }
}
