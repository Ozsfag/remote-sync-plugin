package org.blacksoil.remotesync.ui.enums;

import lombok.Getter;

public enum Action {
  TEST(false, "Test connection", "Testing connection...", "Connection ok.", "Test failed: "),
  SYNC(
      true,
      "Remote sync",
      "Saving settings...",
      "Sync complete.",
      "" // ошибки уже приходят готовыми сообщениями
      );

  @Getter private final boolean requiresValidation;
  @Getter private final String backgroundTitle;
  @Getter private final String startMessage;
  @Getter private final String successMessage;
  @Getter private final String failurePrefix;

  Action(
      boolean requiresValidation,
      String backgroundTitle,
      String startMessage,
      String successMessage,
      String failurePrefix) {
    this.requiresValidation = requiresValidation;
    this.backgroundTitle = backgroundTitle;
    this.startMessage = startMessage;
    this.successMessage = successMessage;
    this.failurePrefix = failurePrefix;
  }
}
