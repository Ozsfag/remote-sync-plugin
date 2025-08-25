package org.blacksoil.remotesync.error;

import com.intellij.openapi.diagnostic.ErrorReportSubmitter;
import com.intellij.openapi.diagnostic.IdeaLoggingEvent;
import com.intellij.openapi.diagnostic.SubmittedReportInfo;
import com.intellij.util.Consumer;
import java.awt.*;
import org.blacksoil.remotesync.error.report.GitHubIssueReporter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RemoteSyncErrorReportSubmitter extends ErrorReportSubmitter {

  private static final String REPO_OWNER = "YOUR_USER";
  private static final String REPO_NAME = "YOUR_REPO";

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
    new Thread(
            () -> {
              try {
                String token = System.getenv("GITHUB_TOKEN");
                boolean success = isSuccess(events, additionalInfo, token);

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
      @NotNull IdeaLoggingEvent[] events, @Nullable String additionalInfo, String token)
      throws Exception {
    if (token == null || token.isBlank()) {
      throw new IllegalStateException("GitHub token is not set in environment.");
    }

    IdeaLoggingEvent event = events[0];
    String title = "[RemoteSync] " + event.getThrowableText().split("\n")[0];
    String body =
        "### Description\n"
            + (additionalInfo != null ? additionalInfo : "_No user description_\n")
            + "\n\n### Stacktrace\n```text\n"
            + event.getThrowableText()
            + "\n```";

    GitHubIssueReporter reporter = new GitHubIssueReporter(token, REPO_OWNER, REPO_NAME);
    return reporter.submitIssue(title, body);
  }
}
