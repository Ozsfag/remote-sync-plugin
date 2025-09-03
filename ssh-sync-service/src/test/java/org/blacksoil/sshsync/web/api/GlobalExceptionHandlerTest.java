package org.blacksoil.sshsync.web.api;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.blacksoil.sshsync.app.service.SyncService;
import org.blacksoil.sshsync.web.sse.SseStreamExecutor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = SshSyncController.class)
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {

  @Autowired MockMvc mvc;

  @SuppressWarnings("removal")
  @MockBean
  SyncService syncService;

  @SuppressWarnings("removal")
  @MockBean
  SseStreamExecutor sseStreamExec;

  @Test
  void bad_request_mapped_to_400() throws Exception {
    doThrow(new IllegalArgumentException("boom"))
        .when(syncService)
        .testConnection("h", "u", "p", "/x");

    String body =
        """
      {"host":"h","username":"u","password":"p","remotePath":"/x"}
    """;

    mvc.perform(post("/api/ssh/test").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("boom"));
  }
}
