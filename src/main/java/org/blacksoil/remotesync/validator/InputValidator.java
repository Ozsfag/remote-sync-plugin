package org.blacksoil.remotesync.validator;

import lombok.experimental.UtilityClass;

@UtilityClass
public class InputValidator {

    public static boolean isValid(String... inputs) {
        for (String input : inputs) {
            if (input == null || input.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
