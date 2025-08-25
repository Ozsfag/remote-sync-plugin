package org.blacksoil.remotesync.ui.actions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RemoteSyncNowAction {
  TEST(false, "Test connection", "Testing connection...", "Connection ok.", "Test failed: "),
  SYNC(
      true,
      "Remote sync",
      "Saving settings...",
      "Sync complete.",
      "" // ошибки уже приходят готовыми сообщениями
      );

  private final boolean requiresValidation;
  private final String backgroundTitle;
  private final String startMessage;
  private final String successMessage;
  private final String failurePrefix;
}
