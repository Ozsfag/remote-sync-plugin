package org.blacksoil.remotesync.core.gitdiff;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import org.blacksoil.remotesync.infrastructure.git.GitCommandExecutor;
import org.blacksoil.remotesync.core.model.DiffResult;
import org.junit.jupiter.api.Test;

class GitDiffDetectorTest {

  @Test
  void testValidDiffResult() {
    GitCommandExecutor mockExecutor = mock(GitCommandExecutor.class);
    when(mockExecutor.runGitCommand(any(), eq("diff"), eq("--name-only"), eq("origin/main")))
        .thenReturn(List.of("file1.java", "file2.java"));
    when(mockExecutor.runGitCommand(
            any(), eq("diff"), eq("--name-only"), eq("--diff-filter=D"), eq("origin/main")))
        .thenReturn(List.of("deleted1.java"));

    DiffResult result = GitDiffDetector.getChangedFiles("/project", "main", mockExecutor);

    assertEquals(2, result.addedOrModified().size());
    assertEquals(1, result.deleted().size());
  }

  @Test
  void testInvalidInput() {
    GitCommandExecutor dummyExecutor = mock(GitCommandExecutor.class);

    DiffResult result = GitDiffDetector.getChangedFiles(null, "main", dummyExecutor);

    assertTrue(result.addedOrModified().isEmpty());
    assertTrue(result.deleted().isEmpty());
    verify(dummyExecutor, never()).runGitCommand(any(), any());
  }
}
