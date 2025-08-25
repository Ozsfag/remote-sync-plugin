package org.blacksoil.remotesync.error.submission;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.intellij.openapi.diagnostic.IdeaLoggingEvent;
import com.intellij.openapi.diagnostic.SubmittedReportInfo;
import com.intellij.util.Consumer;
import java.awt.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

public class RemoteSyncErrorReportSubmitterTest {

  @Test
  void submit_callsReporterAndConsumer() throws InterruptedException {
    System.setProperty("GITHUB_TOKEN", "abc");
    System.setProperty("GITHUB_REPO_OWNER", "owner");
    System.setProperty("GITHUB_REPO_NAME", "repo");

    IdeaLoggingEvent mockEvent = mock(IdeaLoggingEvent.class);
    when(mockEvent.getThrowableText()).thenReturn("RuntimeException: Something went wrong\nStack");

    CountDownLatch latch = new CountDownLatch(1);

    @SuppressWarnings("unchecked")
    Consumer<SubmittedReportInfo> consumer = mock(Consumer.class);
    doAnswer(
            invocation -> {
              latch.countDown();
              return null;
            })
        .when(consumer)
        .consume(any());

    RemoteSyncErrorReportSubmitter submitter = new RemoteSyncErrorReportSubmitter();
    boolean result =
        submitter.submit(
            new IdeaLoggingEvent[] {mockEvent}, "user-provided-info", new Panel(), consumer);

    assertTrue(result);
    boolean completed = latch.await(5, TimeUnit.SECONDS);
    assertTrue(completed, "Callback was not invoked within timeout");
    verify(consumer).consume(any());
  }
}
