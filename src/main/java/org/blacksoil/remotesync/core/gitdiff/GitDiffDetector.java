package org.blacksoil.remotesync.core.gitdiff;

import com.intellij.openapi.diagnostic.Logger;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.blacksoil.remotesync.core.model.DiffResult;
import org.blacksoil.remotesync.infrastructure.git.DefaultGitCommandExecutor;
import org.blacksoil.remotesync.infrastructure.git.GitCommandExecutor;

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

    List<String> output =
        customExecutor.runGitCommand(projectDir, "diff", "--name-status", "origin/" + branch);

    List<String> addedOrModified = new ArrayList<>();
    List<String> deleted = new ArrayList<>();

    for (String line : output) {
      LOG.info("git diff: " + line);
      if (line.startsWith("A") || line.startsWith("M")) {
        addedOrModified.add(line.substring(1).trim());
      } else if (line.startsWith("D")) {
        deleted.add(line.substring(1).trim());
      }
    }

    return new DiffResult(addedOrModified, deleted);
  }
}
