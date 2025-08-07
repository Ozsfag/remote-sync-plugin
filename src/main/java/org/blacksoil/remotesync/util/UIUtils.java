package org.blacksoil.remotesync.util;

import com.intellij.uiDesigner.core.GridConstraints;
import java.awt.*;
import javax.swing.*;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UIUtils {

  public JTextField addLabeledTextField(JPanel panel, int row, String labelText) {
    JLabel label = new JLabel(labelText);
    JTextField field = new JTextField();

    panel.add(label, constraints(row, 0));
    panel.add(field, constraints(row, 1, true));

    return field;
  }

  public GridConstraints constraints(int row, int col) {
    return constraints(row, col, 1, false);
  }

  public GridConstraints constraints(int row, int col, boolean growX) {
    return constraints(row, col, 1, growX);
  }

  public GridConstraints constraints(int row, int col, int colSpan, boolean growX) {
    return new GridConstraints(
        row,
        col,
        1,
        colSpan,
        GridConstraints.ANCHOR_WEST,
        growX ? GridConstraints.FILL_HORIZONTAL : GridConstraints.FILL_NONE,
        growX ? GridConstraints.SIZEPOLICY_WANT_GROW : GridConstraints.SIZEPOLICY_FIXED,
        GridConstraints.SIZEPOLICY_FIXED,
        null,
        null,
        null);
  }
}
