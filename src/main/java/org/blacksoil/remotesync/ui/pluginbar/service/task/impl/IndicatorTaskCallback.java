package org.blacksoil.remotesync.ui.pluginbar.service.task.impl;

import com.intellij.openapi.progress.ProgressIndicator;
import org.blacksoil.remotesync.ui.pluginbar.service.task.api.TaskCallback;

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
