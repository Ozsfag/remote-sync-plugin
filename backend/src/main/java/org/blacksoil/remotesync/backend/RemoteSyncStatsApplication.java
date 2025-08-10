package org.blacksoil.remotesync.backend; // замени на свой пакет

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RemoteSyncStatsApplication {
  public static void main(String[] args) {
    SpringApplication.run(RemoteSyncStatsApplication.class, args);
  }
}
