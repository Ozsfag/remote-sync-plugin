package org.blacksoil.remotesync.ui.pluginbar.application.client.factory;

import lombok.experimental.UtilityClass;

import java.net.http.HttpClient;
import java.time.Duration;

@UtilityClass
public class HttpClientFactory {
  private final HttpClient CLIENT =
      HttpClient.newBuilder()
          .connectTimeout(Duration.ofSeconds(5))
          .followRedirects(HttpClient.Redirect.NORMAL)
          .version(HttpClient.Version.HTTP_1_1)
          .build();


  public HttpClient client() {
    return CLIENT;
  }
}
