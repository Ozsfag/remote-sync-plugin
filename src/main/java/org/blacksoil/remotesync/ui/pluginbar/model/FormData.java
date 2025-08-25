package org.blacksoil.remotesync.ui.pluginbar.model;

import com.intellij.openapi.project.Project;
import org.blacksoil.remotesync.ui.pluginbar.secret.Secrets;
import org.blacksoil.remotesync.ui.pluginbar.settings.RemoteSyncSettings;
import org.jetbrains.annotations.NotNull;

/**
 * Иммутабельный DTO состояния формы. Даёт фабрику из Settings/Secrets и метод persist(...) обратно
 * в Settings/Secrets.
 */
public record FormData(
    String username,
    String ip,
    String password, // хранится только в памяти, в Settings не пишем
    String remotePath,
    String branch) {

  /** Считывает данные из Settings + пароль из Secrets. */
  public static @NotNull FormData from(
      @NotNull Project project, @NotNull RemoteSyncSettings.State s) {
    String u = n(s.username);
    String i = n(s.ip);
    String r = n(s.remotePath);
    String b = n(s.branch);
    if (b.isEmpty()) b = "main";
    String p = Secrets.loadPassword(project, i, u);
    return new FormData(u, i, p, r, b);
  }

  private static String n(String v) {
    return v == null ? "" : v.trim();
  }

  /** Сохраняет данные в Settings и пароль в Secrets. */
  public void persist(@NotNull Project project, @NotNull RemoteSyncSettings settings) {
    RemoteSyncSettings.State s = new RemoteSyncSettings.State();
    s.username = n(username);
    s.ip = n(ip);
    s.remotePath = n(remotePath);
    s.branch = n(branch);
    settings.loadState(s);

    Secrets.savePassword(project, s.ip, s.username, n(password));
  }
}
