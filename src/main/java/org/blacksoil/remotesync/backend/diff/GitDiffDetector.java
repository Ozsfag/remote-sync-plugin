package org.blacksoil.remotesync.backend.diff;

import com.intellij.openapi.diagnostic.Logger;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.blacksoil.remotesync.backend.diff.command.DefaultGitCommandExecutor;
import org.blacksoil.remotesync.backend.diff.command.GitCommandExecutor;
import org.blacksoil.remotesync.backend.validator.GitDiffValidator;
import org.blacksoil.remotesync.common.DiffResult;

@UtilityClass
public class GitDiffDetector {

  private static final GitCommandExecutor executor = new DefaultGitCommandExecutor();
  private final Logger LOG = Logger.getInstance(GitDiffDetector.class);

  public DiffResult getChangedFiles(String projectDir, String branch) {
    return getChangedFiles(projectDir, branch, executor);
  }

  public DiffResult getChangedFiles(
      String projectDir, String branch, GitCommandExecutor customExecutor) {
    if (!GitDiffValidator.isValid(projectDir, branch)) {
      LOG.warn(String.format("Invalid input: projectDir=%s, branch=%s", projectDir, branch));
      return new DiffResult(List.of(), List.of());
    }

    List<String> addedOrModified =
        customExecutor.runGitCommand(projectDir, "diff", "--name-only", "origin/" + branch);
    List<String> deleted =
        customExecutor.runGitCommand(
            projectDir, "diff", "--name-only", "--diff-filter=D", "origin/" + branch);

    return new DiffResult(addedOrModified, deleted);
  }
}
