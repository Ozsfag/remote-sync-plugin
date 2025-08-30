package org.blacksoil.gitdiff.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.blacksoil.gitdiff.app.service.GitDiffService;
import org.blacksoil.shareddto.GitDiffResponse;
import org.blacksoil.gitdiff.app.git.GitCommandExecutor;
import org.junit.jupiter.api.Test;

class GitDiffServiceTest {

  @Test
  void testValidDiffResult() {

    GitDiffService service = mock(GitDiffService.class);
    GitCommandExecutor executor = mock(GitCommandExecutor.class);

    when(executor.runGitCommand(any(), eq("diff"), eq("--name-status"), eq("origin/main")))
        .thenReturn(List.of("A\tfile1.java", "M\tfile2.java", "D\tdeleted1.java"));

    GitDiffResponse result = service.getChangedFiles("/project", "main");

    assertEquals(2, result.addedOrModified().size(), "Expected 2 added/modified files");
    assertEquals(1, result.deleted().size(), "Expected 1 deleted file");
    assertTrue(result.addedOrModified().contains("file1.java"));
    assertTrue(result.addedOrModified().contains("file2.java"));
    assertTrue(result.deleted().contains("deleted1.java"));
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
