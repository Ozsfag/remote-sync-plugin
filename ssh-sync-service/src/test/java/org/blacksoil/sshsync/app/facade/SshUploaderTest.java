package org.blacksoil.sshsync.app.facade;

import static org.mockito.Mockito.*;

import java.util.List;
import java.util.function.Consumer;
import org.blacksoil.sshsync.app.service.SyncService;
import org.junit.jupiter.api.Test;

class SshUploaderTest {

  @Test
  void delegates_to_service() throws Exception {
    SyncService service = mock(SyncService.class);
    SshUploader uploader = new SshUploader(service);

    List<String> files = List.of("a.txt");
    Consumer<String> progress = s -> {};

    uploader.uploadFiles(files, "/local", "~/repo", "h", "u", "p", progress);
    verify(service).uploadFiles(files, "/local", "~/repo", "h", "u", "p", progress);

    uploader.deleteFiles(files, "~/repo", "h", "u", "p", progress);
    verify(service).deleteFiles(files, "~/repo", "h", "u", "p", progress);

    uploader.testConnection("h", "u", "p", "~/repo");
    verify(service).testConnection("h", "u", "p", "~/repo");
  }
}
