package org.blacksoil.remotesync.validator;

public class GitDiffValidator {

  public static boolean isValid(String... inputs) {
    for (String input : inputs) {
      if (input == null || input.trim().isEmpty()) {
        return false;
      }
    }
    return true;
  }
}
