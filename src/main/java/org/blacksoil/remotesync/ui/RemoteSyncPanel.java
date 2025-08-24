package org.blacksoil.remotesync.ui;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import javax.swing.*;
import org.blacksoil.remotesync.ui.enums.Action;
import org.blacksoil.remotesync.ui.model.FormData;
import org.blacksoil.remotesync.ui.report.StatusReporter;
import org.blacksoil.remotesync.ui.service.RemoteSyncService;
import org.blacksoil.remotesync.ui.settings.RemoteSyncSettings;
import org.blacksoil.remotesync.ui.util.Debouncer;
import org.blacksoil.remotesync.ui.view.RemoteSyncView;
import org.blacksoil.remotesync.ui.view.validator.FieldsValidator;
import org.jetbrains.annotations.NotNull;

public final class RemoteSyncPanel implements Disposable {

  // deps
  private final Project project;
  private final RemoteSyncSettings settings;

  // ui
  private final RemoteSyncView view = new RemoteSyncView();
  private final StatusReporter status = new StatusReporter(view);
  private final FieldsValidator validator = new FieldsValidator(view);

  // utils
  private final Debouncer saveDebounce = new Debouncer(400);
  private final AtomicBoolean running = new AtomicBoolean(false);

  /**
   * @param parentDisposable владелец жизненного цикла UI (контент ToolWindow/Configurable)
   */
  public RemoteSyncPanel(@NotNull Project project, @NotNull Disposable parentDisposable) {
    this.project = project;
    this.settings = RemoteSyncSettings.getInstance(project);

    // регистрируемся у родителя, не у Project
    Disposer.register(parentDisposable, this);

    // init form from Settings + Secrets
    view.setData(FormData.from(project, settings.getState()));

    // wire callbacks
    view.onChange(() -> saveDebounce.submit(this::autoPersist));
    view.onTest(() -> runAction(Action.TEST));
    view.onSync(() -> runAction(Action.SYNC));
  }

  public JPanel getContent() {
    return view.getRoot();
  }

  // ================= orchestration =================

  /** Единая точка запуска действий, чтобы не дублировать код. */
  private void runAction(Action action) {
    if (!running.compareAndSet(false, true)) {
      status.info("Another task is already running…");
      return;
    }

    view.setBusy(true);
    status.info(action.getStartMessage());

    if (action.isRequiresValidation()) {
      validator.clear();
      if (!validator.validate()) {
        status.error("Please fill in all required fields.");
        view.setBusy(false);
        running.set(false);
        return;
      }
    }

    applyPendingAndPersist();

    runBackground(
        action.getBackgroundTitle(),
        status::info,
        err -> status.error(action.getFailurePrefix() + err),
        () -> status.ok(action.getSuccessMessage()));
  }

  /** flush debounce → persist текущих значений формы. */
  private void applyPendingAndPersist() {
    if (saveDebounce.hasPending()) status.info("Applying pending changes…");
    saveDebounce.flush();
    autoPersist();
  }

  /** Сохраняет форму в Settings + Secrets. */
  private void autoPersist() {
    FormData d = view.collectData();
    d.persist(project, settings);
  }

  private void runBackground(
      String title, Consumer<String> onStatus, Consumer<String> onError, Runnable onSuccess) {

    new Task.Backgroundable(project, title, true) {
      @Override
      public void run(@NotNull ProgressIndicator indicator) {
        RemoteSyncService.sync(
            project,
            settings.getState(),
            new RemoteSyncService.SyncCallback() {
              @Override
              public void onStatus(String msg) {
                indicator.setText(msg);
                onStatus.accept(msg);
              }

              @Override
              public void onError(String err) {
                onError.accept(err);
              }

              @Override
              public void onComplete() {
                onSuccess.run();
              }
            });
      }

      @Override
      public void onFinished() {
        view.setBusy(false);
        running.set(false);
      }
    }.queue();
  }

  // ================= Disposable =================
  @Override
  public void dispose() {
    saveDebounce.cancel();
  }
}
