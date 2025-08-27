package org.blacksoil.remotesync.core.ssh.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.blacksoil.remotesync.infrastructure.ssh.client.SshClient;
import org.blacksoil.remotesync.infrastructure.ssh.factory.SshClientFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SyncOrchestratorTest {

  // ---- fakes ---------------------------------------------------------------

  static final class FakeClient implements SshClient {
    final List<String> uploaded = new ArrayList<>();
    final List<String> deleted = new ArrayList<>();
    boolean dirExists = true;

    @Override
    public void uploadFile(File localFile, String remoteFilePath) {
      uploaded.add(remoteFilePath);
    }

    @Override
    public void deleteFile(String remoteFilePath) {
      deleted.add(remoteFilePath);
    }

    @Override
    public boolean directoryExists(String remotePath) {
      return dirExists;
    }

    @Override
    public void close() {}
  }

  static final class FakeFactory implements SshClientFactory {
    final FakeClient client = new FakeClient();

    @Override
    public SshClient create(String host, String username, String password) {
      return client;
    }
  }

  // ---- tests ---------------------------------------------------------------

  @Test
  void upload_and_delete_calls_expected_targets(@TempDir Path tmp) throws Exception {
    // создаём реальные файлы
    Path dir = Files.createDirectories(tmp.resolve("dir"));
    Path a = Files.writeString(tmp.resolve("a.txt"), "a");
    Path b = Files.writeString(dir.resolve("b.txt"), "b");

    var factory = new FakeFactory();
    var orchestrator = new SyncOrchestrator(factory);

    // передаём пути относительно root и нормализуем в '/'
    List<String> files =
        List.of(
            tmp.relativize(a).toString().replace('\\', '/'),
            tmp.relativize(b).toString().replace('\\', '/'));

    orchestrator.uploadFiles(files, tmp.toString(), "~/repo", "h", "alice", "pwd");
    assertEquals(
        List.of("/home/alice/repo/a.txt", "/home/alice/repo/dir/b.txt"),
        normalize(factory.client.uploaded));

    orchestrator.deleteFiles(files, "~/repo", "h", "alice", "pwd");
    assertEquals(
        List.of("/home/alice/repo/a.txt", "/home/alice/repo/dir/b.txt"),
        normalize(factory.client.deleted));
  }

  @Test
  void testConnection_throws_if_path_missing() {
    var factory = new FakeFactory();
    factory.client.dirExists = false; // имитация отсутствия директории

    var orchestrator = new SyncOrchestrator(factory);

    // В тестовой среде может прилететь любая «обёртка»; проверяем корневую причину
    Throwable thrown =
        assertThrows(
            Throwable.class, () -> orchestrator.testConnection("h", "alice", "pwd", "~/repo"));

    Throwable root = rootCause(thrown);
    assertInstanceOf(IllegalStateException.class, root);
    assertTrue(root.getMessage().contains("/home/alice/repo"));
  }

  // ---- helpers -------------------------------------------------------------

  private static List<String> normalize(List<String> paths) {
    return paths.stream().map(p -> p.replace('\\', '/')).toList();
  }

  private static Throwable rootCause(Throwable t) {
    Throwable cur = t;
    while (cur.getCause() != null) cur = cur.getCause();
    return cur;
  }
}
