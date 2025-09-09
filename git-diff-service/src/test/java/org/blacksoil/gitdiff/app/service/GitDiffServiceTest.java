package org.blacksoil.gitdiff.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import org.blacksoil.gitdiff.app.git.GitCommandExecutor;
import org.blacksoil.shareddto.gitdiff.GitDiffResponse;
import org.junit.jupiter.api.Test;

class GitDiffServiceTest {

  @Test
  void testValidDiffResult() {
    GitCommandExecutor mockExecutor = mock(GitCommandExecutor.class);

    // Эмулируем вывод git (A/M/D + путь к файлу)
    when(mockExecutor.runGitCommand(anyString(), any(String[].class)))
        .thenReturn(List.of("A src/NewFile.java", "M src/Changed.java", "D src/Removed.java"));

    GitDiffService service = new GitDiffService(mockExecutor);

    GitDiffResponse result = service.getChangedFiles("C:/repo", "main");

    assertNotNull(result);
    assertIterableEquals(List.of("src/NewFile.java", "src/Changed.java"), result.addedOrModified());
    assertIterableEquals(List.of("src/Removed.java"), result.deleted());

    // Если в тесте используется другой путь — поменяй "C:/repo" ниже или замени на anyString()
    verify(mockExecutor).runGitCommand(eq("C:/repo"), any(String[].class));
  }

  @Test
  void testInvalidInput() {
    GitCommandExecutor mockExecutor = mock(GitCommandExecutor.class);
    GitDiffService service = new GitDiffService(mockExecutor);

    GitDiffResponse result = service.getChangedFiles(null, "main");

    assertTrue(result.addedOrModified().isEmpty());
    assertTrue(result.deleted().isEmpty());
    verify(mockExecutor, never()).runGitCommand(any(), any());
  }
}
