package org.blacksoil.remotesync.validator;

import javax.swing.*;

public class PanelValidator {

  public static boolean isValid(JTextField... fields) {
    for (JTextField field : fields) {
      if (field.getText() != null && field.getText().trim().isBlank()) {
        return false;
      }
    }
    return true;
  }
}
