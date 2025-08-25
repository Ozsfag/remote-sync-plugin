package org.blacksoil.remotesync.error.report;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

public record GitHubIssueReporter(String token, String repoOwner, String repoName) {

  public boolean submitIssue(String title, String body) throws Exception {
    String json =
        new JSONObject()
            .put("title", title)
            .put("body", body)
            .put("labels", new JSONArray().put("bug").put("plugin"))
            .toString();

    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(
                URI.create(
                    String.format(
                        "https://api.github.com/%s/%s/issues", repoOwner, repoName)))
            .header("Authorization", "token " + token)
            .header("Accept", "application/vnd.github.v3+json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

    HttpResponse<String> response =
        HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

    return response.statusCode() == 201;
  }
}
