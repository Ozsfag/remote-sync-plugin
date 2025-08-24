package org.blacksoil.remotesync.backend.validator;

import lombok.experimental.UtilityClass;

@UtilityClass
public class GitDiffValidator {

  public boolean isValid(String... inputs) {
    for (String input : inputs) {
      if (input == null || input.trim().isEmpty()) {
        return false;
      }
    }
    return true;
  }
}
