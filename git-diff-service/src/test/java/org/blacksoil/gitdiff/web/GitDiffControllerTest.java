package org.blacksoil.gitdiff.web;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;
import org.blacksoil.gitdiff.app.service.GitDiffService;
import org.blacksoil.shareddto.gitdiff.GitDiffResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@WebMvcTest(controllers = GitDiffController.class)
@Import(GitDiffControllerTest.TestAdvice.class)
class GitDiffControllerTest {

  private static final String PATH = "/api/git/diff";
  @Autowired MockMvc mvc;

  @SuppressWarnings("removal")
  @MockBean
  GitDiffService gitDiffService;

  @Test
  @DisplayName("POST /api/git/diff → 200 OK и корректный JSON")
  void diff_ok() throws Exception {
    when(gitDiffService.getChangedFiles(anyString(), anyString()))
        .thenReturn(new GitDiffResponse(List.of("src/App.java", "README.md"), List.of("old.txt")));

    String reqBody =
        """
      {"projectDir":"/repo/app","branch":"origin/main"}
    """;

    mvc.perform(post(PATH).contentType(MediaType.APPLICATION_JSON).content(reqBody))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.addedOrModified[0]").value("src/App.java"))
        .andExpect(jsonPath("$.addedOrModified[1]").value("README.md"))
        .andExpect(jsonPath("$.deleted[0]").value("old.txt"));
  }

  @Test
  @DisplayName("POST /api/git/diff, сервис бросает IAE → 400 и {error}")
  void diff_badRequest() throws Exception {
    doThrow(new IllegalArgumentException("Not a git repository"))
        .when(gitDiffService)
        .getChangedFiles("/bad", "origin/main");

    String reqBody =
        """
      {"projectDir":"/bad","branch":"origin/main"}
    """;

    mvc.perform(post(PATH).contentType(MediaType.APPLICATION_JSON).content(reqBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value("Not a git repository"));
  }

  @RestControllerAdvice
  static class TestAdvice {
    @ExceptionHandler(IllegalArgumentException.class)
    public org.springframework.http.ResponseEntity<Map<String, Object>> badReq(
        IllegalArgumentException ex) {
      return org.springframework.http.ResponseEntity.badRequest()
          .body(Map.of("error", ex.getMessage()));
    }
  }
}
