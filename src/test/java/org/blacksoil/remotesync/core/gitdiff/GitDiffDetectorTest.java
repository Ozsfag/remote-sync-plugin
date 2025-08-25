package org.blacksoil.remotesync.core.gitdiff;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import org.blacksoil.remotesync.core.model.DiffResult;
import org.blacksoil.remotesync.infrastructure.git.GitCommandExecutor;
import org.junit.jupiter.api.Test;

class GitDiffDetectorTest {

  @Test
  void testValidDiffResult() {
    GitCommandExecutor mockExecutor = mock(GitCommandExecutor.class);

    when(mockExecutor.runGitCommand(any(), eq("diff"), eq("--name-status"), eq("origin/main")))
        .thenReturn(List.of("A\tfile1.java", "M\tfile2.java", "D\tdeleted1.java"));

    DiffResult result = GitDiffDetector.getChangedFiles("/project", "main", mockExecutor);

    assertEquals(2, result.addedOrModified().size(), "Expected 2 added/modified files");
    assertEquals(1, result.deleted().size(), "Expected 1 deleted file");
    assertTrue(result.addedOrModified().contains("file1.java"));
    assertTrue(result.addedOrModified().contains("file2.java"));
    assertTrue(result.deleted().contains("deleted1.java"));
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
