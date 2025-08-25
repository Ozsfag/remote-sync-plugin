package org.blacksoil.remotesync.error.submission;

import com.intellij.openapi.diagnostic.ErrorReportSubmitter;
import com.intellij.openapi.diagnostic.IdeaLoggingEvent;
import com.intellij.openapi.diagnostic.SubmittedReportInfo;
import com.intellij.util.Consumer;
import java.awt.*;
import java.net.http.HttpClient;
import org.blacksoil.remotesync.error.config.GitHubConfig;
import org.blacksoil.remotesync.error.report.GitHubIssueReporter;
import org.blacksoil.remotesync.error.report.IssueReporter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RemoteSyncErrorReportSubmitter extends ErrorReportSubmitter {

  @NotNull
  @Override
  public String getReportActionText() {
    return "Report to GitHub (RemoteSync)";
  }

  @Override
  public boolean submit(
      @NotNull IdeaLoggingEvent[] events,
      @Nullable String additionalInfo,
      @NotNull Component parentComponent,
      @NotNull Consumer<? super SubmittedReportInfo> consumer) {

    if (events.length == 0) {
      consumer.consume(
          new SubmittedReportInfo(
              null, "No event information", SubmittedReportInfo.SubmissionStatus.FAILED));
      return false;
    }

    new Thread(
            () -> {
              try {
                GitHubConfig config = GitHubConfig.fromSystemProperties();
                IssueReporter reporter =
                    new GitHubIssueReporter(config, HttpClient.newHttpClient());

                boolean success = isSuccess(events[0], additionalInfo, reporter);

                consumer.consume(
                    new SubmittedReportInfo(
                        null,
                        success ? "Issue created" : "GitHub API failed",
                        success
                            ? SubmittedReportInfo.SubmissionStatus.NEW_ISSUE
                            : SubmittedReportInfo.SubmissionStatus.FAILED));

              } catch (Exception e) {
                consumer.consume(
                    new SubmittedReportInfo(
                        null,
                        "Failed to submit: " + e.getMessage(),
                        SubmittedReportInfo.SubmissionStatus.FAILED));
              }
            })
        .start();

    return true;
  }

  private static boolean isSuccess(
      @NotNull IdeaLoggingEvent event, @Nullable String additionalInfo, IssueReporter reporter)
      throws Exception {

    String throwableText = event.getThrowableText();
    if (throwableText.isBlank()) {
      throwableText = event.getMessage() != null ? event.getMessage() : "Unknown error";
    }

    String title = "[RemoteSync] " + throwableText.split("\n")[0];
    String body =
        "### Description\n"
            + (additionalInfo != null ? additionalInfo : "_No user description_\n")
            + "\n\n### Stacktrace\n```text\n"
            + throwableText
            + "\n```";

    return reporter.submitIssue(title, body);
  }
}
