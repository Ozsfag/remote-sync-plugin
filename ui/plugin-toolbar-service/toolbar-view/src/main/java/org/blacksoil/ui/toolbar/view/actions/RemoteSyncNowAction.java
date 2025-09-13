package org.blacksoil.ui.toolbar.view.actions;

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
      ""
      );

  private final boolean requiresValidation;
  private final String backgroundTitle;
  private final String startMessage;
  private final String successMessage;
  private final String failurePrefix;
}
