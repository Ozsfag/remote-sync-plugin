package org.blacksoil.remotesync.error.report;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.blacksoil.remotesync.error.config.GitHubConfig;
import org.json.JSONArray;
import org.json.JSONObject;

public record GitHubIssueReporter(GitHubConfig config, HttpClient httpClient) implements IssueReporter {

    @Override
    public boolean submitIssue(String title, String body) throws Exception {
        String json = new JSONObject()
                .put("title", title)
                .put("body", body)
                .put("labels", new JSONArray().put("bug").put("plugin"))
                .toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("https://api.github.com/repos/%s/%s/issues",
                        config.owner(), config.repo())))
                .header("Authorization", "token " + config.token())
                .header("Accept", "application/vnd.github.v3+json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 201;
    }
}
