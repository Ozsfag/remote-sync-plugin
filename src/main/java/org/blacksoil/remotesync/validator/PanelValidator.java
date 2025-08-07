package org.blacksoil.remotesync.validator;

import javax.swing.*;

public class PanelValidator {

  public static boolean isValid(JTextField... fields) {
    for (JTextField field : fields) {
      if (field.getText().trim().isEmpty()) {
        return false;
      }
    }
    return true;
  }
}
