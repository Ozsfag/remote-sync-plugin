package org.blacksoil.sshsync.web.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.blacksoil.sshsync.app.service.SyncService;
import org.blacksoil.sshsync.web.sse.SseStreamExecutor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = SshSyncController.class)
class SshSyncControllerWebTest {

  @Autowired MockMvc mvc;

  @SuppressWarnings("removal")
  @MockBean
  SyncService syncService;

  @SuppressWarnings("removal")
  @MockBean
  SseStreamExecutor sseExec;

  @Test
  @DisplayName("POST /api/ssh/test -> 200 OK и делегирование в SyncService")
  void test_ok() throws Exception {
    doNothing().when(syncService).testConnection("h", "alice", "pwd", "~/repo");

    String body =
        """
      {"host":"h","username":"alice","password":"pwd","remotePath":"~/repo"}
    """;

    mvc.perform(post("/api/ssh/test").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.ok").value(true));

    verify(syncService).testConnection("h", "alice", "pwd", "~/repo");
  }

  @Test
  @DisplayName("POST /api/ssh/upload?stream=false -> 202 Accepted")
  void upload_sync() throws Exception {
    ResponseEntity<?> accepted = ResponseEntity.accepted().body(Map.of("ok", true));
    doReturn(accepted).when(sseExec).run(eq(false), eq("upload"), ArgumentMatchers.any());

    String uploadJson =
        """
      {
        "host":"h","username":"alice","password":"pwd",
        "remotePath":"~/repo","localRoot":"/tmp/build",
        "relativePaths":["a.txt","dir/b.txt"]
      }
    """;
    MockMultipartFile uploadRequest =
        new MockMultipartFile(
            "uploadRequest",
            "uploadRequest",
            MediaType.APPLICATION_JSON_VALUE,
            uploadJson.getBytes(StandardCharsets.UTF_8));

    mvc.perform(multipart("/api/ssh/upload").file(uploadRequest).param("stream", "false"))
        .andExpect(status().isAccepted())
        .andExpect(jsonPath("$.ok").value(true));

    verify(sseExec).run(eq(false), eq("upload"), any());
  }

  @Test
  @DisplayName("POST /api/ssh/upload?stream=true -> 200 OK (SSE)")
  void upload_stream() throws Exception {
    ResponseEntity<?> okSse =
        ResponseEntity.ok().contentType(MediaType.TEXT_EVENT_STREAM).body("sse");
    doReturn(okSse)
        .when(sseExec)
        .run(eq(true), eq("upload"), ArgumentMatchers.any());

    String uploadJson =
        """
      {
        "host":"h","username":"alice","password":"pwd",
        "remotePath":"~/repo","localRoot":"/tmp/build",
        "relativePaths":["a.txt"]
      }
    """;
    MockMultipartFile uploadRequest =
        new MockMultipartFile(
            "uploadRequest",
            "uploadRequest",
            MediaType.APPLICATION_JSON_VALUE,
            uploadJson.getBytes(StandardCharsets.UTF_8));

    mvc.perform(multipart("/api/ssh/upload").file(uploadRequest).param("stream", "true"))
        .andExpect(status().isOk());

    verify(sseExec).run(eq(true), eq("upload"), any());
  }

  @Test
  @DisplayName("POST /api/ssh/delete?stream=false -> 202 Accepted")
  void delete_sync() throws Exception {
    ResponseEntity<?> accepted = ResponseEntity.accepted().body(Map.of("ok", true));
    doReturn(accepted)
        .when(sseExec)
        .run(eq(false), eq("delete"), ArgumentMatchers.any());

    String body =
        """
      {"host":"h","username":"alice","password":"pwd",
       "remotePath":"~/repo","relativePaths":["a.txt","dir/b.txt"]}
    """;

    mvc.perform(
            post("/api/ssh/delete?stream=false")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isAccepted())
        .andExpect(jsonPath("$.ok").value(true));

    verify(sseExec).run(eq(false), eq("delete"), any());
  }

  @Test
  @DisplayName("POST /api/ssh/delete?stream=true -> 200 OK (SSE)")
  void delete_stream() throws Exception {
    ResponseEntity<?> okSse =
        ResponseEntity.ok().contentType(MediaType.TEXT_EVENT_STREAM).body("sse");
    doReturn(okSse)
        .when(sseExec)
        .run(eq(true), eq("delete"), ArgumentMatchers.any());

    String body =
        """
      {"host":"h","username":"alice","password":"pwd",
       "remotePath":"~/repo","relativePaths":["a.txt"]}
    """;

    mvc.perform(
            post("/api/ssh/delete?stream=true")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isOk());

    verify(sseExec).run(eq(true), eq("delete"), any());
  }
}
