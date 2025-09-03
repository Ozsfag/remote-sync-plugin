package org.blacksoil.remotesync.ui.pluginbar.app.validator;

import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class SyncValidator {

  public boolean isNotValid(Object... inputs) {
    for (Object input : inputs) {
      if (input == null) {
        return true;
      }
    }
    return false;
  }

  @SafeVarargs
  public boolean isEmpty(List<String>... inputs){
    for (List<String> input : inputs) {
      if (input.isEmpty()) {
        return true;
      }
    }
    return false;
  }
}
