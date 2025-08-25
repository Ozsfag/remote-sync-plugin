package org.blacksoil.remotesync.ui.components;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import javax.swing.*;
import org.blacksoil.remotesync.ui.actions.RemoteSyncNowAction;
import org.blacksoil.remotesync.ui.model.FormData;
import org.blacksoil.remotesync.ui.service.StatusReporter;
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

  public RemoteSyncPanel(@NotNull Project project, @NotNull Disposable parentDisposable) {
    this.project = project;
    this.settings = RemoteSyncSettings.getInstance(project);

    Disposer.register(parentDisposable, this);

    view.setData(FormData.from(project, settings.getState()));

    view.onChange(() -> saveDebounce.submit(this::autoPersist));
    view.onTest(this::runTestConnection);
    view.onSync(this::runSync);
  }

  public JPanel getContent() {
    return view.getRoot();
  }

  // ================= orchestration =================

  private void runTestConnection() {
    if (!validator.validate()) {
      status.error("Please fill in all required fields.");
      return;
    }

    applyPendingAndPersist();

    runBackground(
        "Testing SSH connection...",
        status::info,
        err -> status.error("Connection failed: " + err),
        () -> status.ok("Connection successful"),
        RemoteSyncNowAction.TEST);
  }

  private void runSync() {
    if (!running.compareAndSet(false, true)) {
      status.info("Another task is already running…");
      return;
    }

    view.setBusy(true);
    status.info("Syncing...");

    if (!validator.validate()) {
      status.error("Please fill in all required fields.");
      view.setBusy(false);
      running.set(false);
      return;
    }

    applyPendingAndPersist();

    runBackground(
        "Running sync...",
        status::info,
        err -> status.error("Sync failed: " + err),
        () -> status.ok("Sync complete"),
        RemoteSyncNowAction.SYNC);
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
    ApplicationManager.getApplication().executeOnPooledThread(() -> d.persist(project, settings));
  }

  private void runBackground(
      String title,
      Consumer<String> onStatus,
      Consumer<String> onError,
      Runnable onSuccess,
      RemoteSyncNowAction remoteSyncNowAction) {

    new Task.Backgroundable(project, title, true) {
      @Override
      public void run(@NotNull ProgressIndicator indicator) {
        if (remoteSyncNowAction == RemoteSyncNowAction.TEST) {
          RemoteSyncService.testConnection(
              project,
              settings.getState(),
              new RemoteSyncService.SyncCallback() {
                public void onStatus(String msg) {
                  indicator.setText(msg);
                  onStatus.accept(msg);
                }

                public void onError(String err) {
                  onError.accept(err);
                }

                public void onComplete() {
                  onSuccess.run();
                }
              });
        } else {
          RemoteSyncService.sync(
              project,
              settings.getState(),
              new RemoteSyncService.SyncCallback() {
                public void onStatus(String msg) {
                  indicator.setText(msg);
                  onStatus.accept(msg);
                }

                public void onError(String err) {
                  onError.accept(err);
                }

                public void onComplete() {
                  onSuccess.run();
                }
              });
        }
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
