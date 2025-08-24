package org.blacksoil.remotesync.ui.view.validator;

import javax.swing.*;
import org.blacksoil.remotesync.ui.view.RemoteSyncView;

/** Простая валидация формы + подсветка ошибок через outline. */
public record FieldsValidator(RemoteSyncView view) {

    private static boolean notEmpty(JTextField f) {
    String s = f.getText();
    return s != null && !s.trim().isEmpty();
  }

  private static void clear(JComponent c) {
    c.putClientProperty("JComponent.outline", null);
  }

  /** Валидирует и подсвечивает проблемные поля. */
  public boolean validate() {
    boolean u = notEmpty(view.getUsernameField());
    boolean i = notEmpty(view.getIpField());
    boolean p = notEmpty(view.getPasswordField());
    boolean r = notEmpty(view.getRemotePathField());
    boolean b = notEmpty(view.getBranchField());

    view.markError(view.getUsernameField(), !u);
    view.markError(view.getIpField(), !i);
    view.markError(view.getPasswordField(), !p);
    view.markError(view.getRemotePathField(), !r);
    view.markError(view.getBranchField(), !b);

    return u && i && p && r && b;
  }

  /** Сбрасывает подсветку у всех полей. Полезно перед повторной валидацией. */
  public void clear() {
    clear(view.getUsernameField());
    clear(view.getIpField());
    clear(view.getPasswordField());
    clear(view.getRemotePathField());
    clear(view.getBranchField());
  }
}
