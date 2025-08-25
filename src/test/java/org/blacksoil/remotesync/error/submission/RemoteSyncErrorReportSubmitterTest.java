package org.blacksoil.remotesync.error.submission;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.intellij.openapi.diagnostic.IdeaLoggingEvent;
import com.intellij.openapi.diagnostic.SubmittedReportInfo;
import com.intellij.util.Consumer;
import java.awt.*;
import org.junit.jupiter.api.Test;

public class RemoteSyncErrorReportSubmitterTest {

  @Test
  void submit_callsReporterAndConsumer() throws InterruptedException {
    System.setProperty("GITHUB_TOKEN", "abc");
    System.setProperty("GITHUB_REPO_OWNER", "owner");
    System.setProperty("GITHUB_REPO_NAME", "repo");

    IdeaLoggingEvent mockEvent = mock(IdeaLoggingEvent.class);
    when(mockEvent.getThrowableText()).thenReturn("RuntimeException: Something went wrong\nStack");

    @SuppressWarnings("unchecked")
    Consumer<SubmittedReportInfo> consumer = mock(Consumer.class);

    // Обернём submit() вызов, не тестируя GitHub напрямую
    RemoteSyncErrorReportSubmitter submitter = spy(new RemoteSyncErrorReportSubmitter());

    // Сами тестируем только внешний запуск — GitHubIssueReporter уже протестирован отдельно
    boolean result =
        submitter.submit(
            new IdeaLoggingEvent[] {mockEvent}, "user-provided-info", new Panel(), consumer);

    assertTrue(result);
    Thread.sleep(1000);
    verify(consumer, atLeastOnce()).consume(any());
  }
}
