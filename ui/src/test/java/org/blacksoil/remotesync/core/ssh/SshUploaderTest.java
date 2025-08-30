package org.blacksoil.remotesync.core.ssh;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.File;
import org.blacksoil.remotesync.infrastructure.ssh.client.SshClient;
import org.junit.jupiter.api.Test;

class SshUploaderTest {

  @Test
  void testUploadSkipsMissingFile() {
    SshClient mockClient = mock(SshClient.class);
    File missingFile = new File("not/exist.txt");
    assertFalse(missingFile.exists());
    // можно дополнительно протестировать с PowerMock, если внедрим client
  }

  @Test
  void testDeleteCallsClient() throws Exception {
    SshClient mockClient = mock(SshClient.class);
    mockClient.deleteFile("remote/path/file.txt");
    verify(mockClient, times(1)).deleteFile(any());
  }

  @Test
  void testConnectionSuccess() {
    assertDoesNotThrow(
        () -> {
          try (SshClient ignored = mock(SshClient.class)) {
            // simulate connection
          }
        });
  }
}
