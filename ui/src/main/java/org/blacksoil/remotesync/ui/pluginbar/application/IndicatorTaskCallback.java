package org.blacksoil.remotesync.ui.pluginbar.application;

import com.intellij.openapi.progress.ProgressIndicator;

public record IndicatorTaskCallback(ProgressIndicator indicator, TaskCallback delegate)
    implements TaskCallback {

  @Override
  public void onStatus(String message) {
    if (indicator != null) indicator.setText(message);
    if (delegate != null) delegate.onStatus(message);
  }

  @Override
  public void onError(String error) {
    if (delegate != null) delegate.onError(error);
  }

  @Override
  public void onComplete() {
    if (delegate != null) delegate.onComplete();
  }
}
