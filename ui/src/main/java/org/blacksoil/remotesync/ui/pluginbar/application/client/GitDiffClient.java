package org.blacksoil.remotesync.ui.pluginbar.application.client;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.blacksoil.dto.GitDiffRequest;
import org.blacksoil.dto.GitDiffResponse;
import org.blacksoil.remotesync.ui.pluginbar.application.client.factory.HttpClientFactory;
import org.json.JSONArray;
import org.json.JSONObject;

public record GitDiffClient(String baseUrl) {

  public GitDiffResponse getDiff(GitDiffRequest request) throws Exception {
    JSONObject body =
        new JSONObject().put("projectDir", request.projectDir()).put("branch", request.branch());

    HttpRequest httpRequest =
        HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + "/diff"))
            .timeout(Duration.ofSeconds(10))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
            .build();

    HttpResponse<String> httpResponse =
        HttpClientFactory.createClient()
            .send(httpRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

    if (httpResponse.statusCode() / 100 != 2) {
      throw new IllegalStateException(
          "git-diff-service HTTP " + httpResponse.statusCode() + " body=" + httpResponse.body());
    }

    JSONObject json = new JSONObject(httpResponse.body());
    List<String> addedOrModified = toStringList(json.optJSONArray("addedOrModified"));
    List<String> deleted = toStringList(json.optJSONArray("deleted"));
    return new GitDiffResponse(addedOrModified, deleted);
  }

  private static List<String> toStringList(JSONArray arr) {
    List<String> list = new ArrayList<>();
    if (arr == null) return list;
    for (int i = 0; i < arr.length(); i++) {
      list.add(arr.optString(i));
    }
    return list;
  }
}
