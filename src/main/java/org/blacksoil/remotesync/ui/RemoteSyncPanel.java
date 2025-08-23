package org.blacksoil.remotesync.ui;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import java.awt.*;
import javax.swing.*;
import org.blacksoil.remotesync.service.RemoteSyncService;
import org.blacksoil.remotesync.settings.RemoteSyncSettings;
import org.blacksoil.remotesync.ui.model.FormData;
import org.blacksoil.remotesync.ui.view.RemoteSyncView;
import org.blacksoil.remotesync.validator.PanelValidator;
import org.jetbrains.annotations.NotNull;

public class RemoteSyncPanel {

  private final Project project;
  private final RemoteSyncSettings settings;

  private final RemoteSyncView view = new RemoteSyncView();
  private final Debouncer saveDebounce = new Debouncer(400);

  public RemoteSyncPanel(Project project) {
    this.project = project;
    this.settings = RemoteSyncSettings.getInstance(project);

    // init data
    FormData data = FormData.from(project, settings.getState());
    view.setData(data);

    // wire callbacks
    view.onChange(() -> saveDebounce.call(this::autoPersist));
    view.onTest(this::onTestConnection);
    view.onSync(this::onSaveAndSync);
  }

  public JPanel getContent() {
    return (JPanel) view.getComponent();
  }

  // ---------- actions ----------

  private void onTestConnection() {
    view.setStatus("Testing connection...", null);
    view.setBusy(true);
    autoPersist(); // ensure latest values saved
    runSync(
            "Test connection",
            () -> view.setStatus("Connection ok.", JBColor.GREEN),
            err -> view.setStatus("Test failed: " + err, JBColor.RED));
  }

  private void onSaveAndSync() {
    view.setStatus("Saving settings...", null);
    view.setBusy(true);

    if (!validateFields()) {
      view.setStatus("Please fill in all required fields.", JBColor.RED);
      view.setBusy(false);
      return;
    }

    autoPersist();
    runSync(
            "Remote sync",
            () -> view.setStatus("Sync complete.", JBColor.GREEN),
            err -> view.setStatus(err, JBColor.RED));
  }

  // ---------- helpers ----------

  /** Сохраняет текущее состояние формы в Settings и Secrets. */
  private void autoPersist() {
    FormData d = view.collectData();
    d.persist(project, settings);
  }

  private boolean validateFields() {
    boolean ok =
            PanelValidator.isValid(
                    view.usernameField(),
                    view.ipField(),
                    view.passwordField(),
                    view.remotePathField(),
                    view.branchField());

    // Лёгкая визуальная подсветка
    view.markError(view.usernameField(), view.usernameField().getText().trim().isEmpty());
    view.markError(view.ipField(), view.ipField().getText().trim().isEmpty());
    view.markError(
            view.passwordField(),
            new String(((JPasswordField) view.passwordField()).getPassword()).trim().isEmpty());
    view.markError(
            view.remotePathField(), view.remotePathField().getText().trim().isEmpty());
    view.markError(view.branchField(), view.branchField().getText().trim().isEmpty());
    return ok;
  }

  private void runSync(
          String title, Runnable onOk, java.util.function.Consumer<String> onErr) {
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
                    view.setStatus(msg, null);
                  }

                  @Override
                  public void onError(String err) {
                    onErr.accept(err);
                  }

                  @Override
                  public void onComplete() {
                    onOk.run();
                  }
                });
      }

      @Override
      public void onFinished() {
        view.setBusy(false);
      }
    }.queue();
  }

  // ---------- util: Swing debouncer ----------
  private static final class Debouncer {
    private final int delayMs;
    private Timer timer;

    Debouncer(int delayMs) {
      this.delayMs = delayMs;
    }

    void call(Runnable r) {
      if (timer != null) timer.stop();
      timer = new Timer(delayMs, e -> r.run());
      timer.setRepeats(false);
      timer.start();
    }
  }
}
