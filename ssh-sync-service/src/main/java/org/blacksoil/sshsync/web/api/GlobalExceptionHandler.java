package org.blacksoil.sshsync.web.api;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> badRequest(IllegalArgumentException ex) {
    return Map.of("error", ex.getMessage());
  }
}
