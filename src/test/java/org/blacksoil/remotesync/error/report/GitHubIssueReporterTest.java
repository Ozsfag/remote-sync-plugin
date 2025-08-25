package org.blacksoil.remotesync.error.report;

import org.blacksoil.remotesync.error.config.GitHubConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.http.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GitHubIssueReporterTest {

    private HttpClient httpClient;
    private HttpResponse<String> response;

    private GitHubConfig config;

    @BeforeEach
    void setup() {
        httpClient = mock(HttpClient.class);
        response = mock(HttpResponse.class);
        config = new GitHubConfig("token123", "owner", "repo");
    }

    @Test
    void submitIssue_success() throws Exception {
        when(response.statusCode()).thenReturn(201);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(response);

        GitHubIssueReporter reporter = new GitHubIssueReporter(config, httpClient);
        boolean result = reporter.submitIssue("Title", "Body");

        assertTrue(result);
    }

    @Test
    void submitIssue_failure() throws Exception {
        when(response.statusCode()).thenReturn(400);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(response);

        GitHubIssueReporter reporter = new GitHubIssueReporter(config, httpClient);
        boolean result = reporter.submitIssue("Title", "Body");

        assertFalse(result);
    }
}
