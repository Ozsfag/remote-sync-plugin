package org.blacksoil.remotesync.validator;

import javax.swing.*;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PanelValidator {

  public boolean isValid(JTextField... fields) {
    for (JTextField field : fields) {
      if (field.getText().trim().isEmpty()) {
        return false;
      }
    }
    return true;
  }
}
