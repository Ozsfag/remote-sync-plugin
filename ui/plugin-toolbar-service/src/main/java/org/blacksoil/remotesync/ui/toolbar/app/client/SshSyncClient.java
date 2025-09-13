package org.blacksoil.remotesync.ui.toolbar.app.client;

import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.blacksoil.remotesync.ui.toolbar.app.client.factory.HttpClientFactory;
import org.blacksoil.shareddto.sshsync.DeleteRequest;
import org.blacksoil.shareddto.sshsync.TestRequest;
import org.blacksoil.shareddto.sshsync.UploadRequest;
import org.json.JSONArray;
import org.json.JSONObject;

public record SshSyncClient(String baseUrl) {

  public void testConnection(TestRequest request) throws Exception {
    JSONObject body =
        new JSONObject()
            .put("host", request.host())
            .put("username", request.username())
            .put("password", request.password())
            .put("remotePath", request.remotePath());
    post("/api/ssh/test", body);
  }

  public void uploadFiles(UploadRequest request) throws Exception {
    JSONObject body =
        new JSONObject()
            .put("host", request.host())
            .put("username", request.username())
            .put("password", request.password())
            .put("remotePath", request.remotePath())
            .put("localRoot", request.localRoot())
            .put("files", new JSONArray(request.files()));
    post("/api/ssh/upload", body);
  }

  public void deleteFiles(DeleteRequest request) throws Exception {
    JSONObject body =
        new JSONObject()
            .put("host", request.host())
            .put("username", request.username())
            .put("password", request.password())
            .put("remotePath", request.remotePath())
            .put("files", new JSONArray(request.files()));
    post("/api/ssh/delete", body);
  }

  private void post(String path, JSONObject body) throws Exception {
    HttpRequest httpRequest =
        HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + path))
            .timeout(Duration.ofSeconds(120))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
            .build();

    HttpResponse<String> httpResponse;
    try {
      httpResponse =
          HttpClientFactory.createClient()
              .send(httpRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    } catch (ConnectException e) {
      throw new IllegalStateException("Cannot connect to ssh-sync-service at " + baseUrl + path, e);
    }

    if (httpResponse.statusCode() / 100 != 2) {
      throw new IllegalStateException(
          "ssh-sync-service HTTP " + httpResponse.statusCode() + " body=" + httpResponse.body());
    }
  }
}
