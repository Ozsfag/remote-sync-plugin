package org.blacksoil.sshsync.web.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = SshSyncController.class)
class SshSyncControllerWebTest {

  @Autowired MockMvc mvc;

  @MockBean SyncService syncService;
  @MockBean SseStreamExecutor sseExec;

  @Test
  @DisplayName("POST /api/ssh/test -> 200 OK и вызов SyncService.testConnection")
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

    String json =
        """
      {
        "host":"h",
        "username":"alice",
        "password":"pwd",
        "remotePath":"~/repo",
        "localRoot":"/tmp/build",
        "files":["a.txt","dir/b.txt"]
      }
      """;

    mvc.perform(
            post("/api/ssh/upload")
                .param("stream", "false")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andExpect(status().isAccepted())
        .andExpect(jsonPath("$.ok").value(true));

    verify(sseExec).run(eq(false), eq("upload"), any());
  }

  @Test
  @DisplayName("POST /api/ssh/upload?stream=true -> 200 OK (SSE)")
  void upload_stream() throws Exception {
    ResponseEntity<?> okSse =
        ResponseEntity.ok().contentType(MediaType.TEXT_EVENT_STREAM).body("sse");
    doReturn(okSse).when(sseExec).run(eq(true), eq("upload"), any());

    String json =
        """
      {
        "host":"h",
        "username":"alice",
        "password":"pwd",
        "remotePath":"~/repo",
        "localRoot":"/tmp/build",
        "files":["a.txt"]
      }
      """;

    mvc.perform(
            post("/api/ssh/upload")
                .param("stream", "true")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andExpect(status().isOk());

    verify(sseExec).run(eq(true), eq("upload"), any());
  }

  @Test
  @DisplayName("POST /api/ssh/delete?stream=false -> 202 Accepted")
  void delete_sync() throws Exception {
    ResponseEntity<?> accepted = ResponseEntity.accepted().body(Map.of("ok", true));
    doReturn(accepted).when(sseExec).run(eq(false), eq("delete"), any());

    String json =
        """
      {
        "host":"h",
        "username":"alice",
        "password":"pwd",
        "remotePath":"~/repo",
        "files":["a.txt","dir/b.txt"]
      }
      """;

    mvc.perform(
            post("/api/ssh/delete")
                .param("stream", "false")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andExpect(status().isAccepted())
        .andExpect(jsonPath("$.ok").value(true));

    verify(sseExec).run(eq(false), eq("delete"), any());
  }

  @Test
  @DisplayName("POST /api/ssh/delete?stream=true -> 200 OK (SSE)")
  void delete_stream() throws Exception {
    ResponseEntity<?> okSse =
        ResponseEntity.ok().contentType(MediaType.TEXT_EVENT_STREAM).body("sse");
    doReturn(okSse).when(sseExec).run(eq(true), eq("delete"), any());

    String json =
        """
      {
        "host":"h",
        "username":"alice",
        "password":"pwd",
        "remotePath":"~/repo",
        "files":["a.txt"]
      }
      """;

    mvc.perform(
            post("/api/ssh/delete")
                .param("stream", "true")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andExpect(status().isOk());

    verify(sseExec).run(eq(true), eq("delete"), any());
  }
}
