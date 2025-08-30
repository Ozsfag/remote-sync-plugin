package org.blacksoil.remotesync.ui.pluginbar.application.client.factory;

import java.net.http.HttpClient;
import java.time.Duration;
import lombok.experimental.UtilityClass;

@UtilityClass
public class HttpClientFactory {

  public HttpClient createClient() {
    return HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5))
        .followRedirects(HttpClient.Redirect.NORMAL)
        .version(HttpClient.Version.HTTP_1_1)
        .build();
  }
}
