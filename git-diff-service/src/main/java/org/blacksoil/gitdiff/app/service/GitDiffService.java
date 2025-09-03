package org.blacksoil.gitdiff.app.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.blacksoil.gitdiff.app.git.GitCommandExecutor;
import org.blacksoil.gitdiff.app.validator.GitDiffValidator;
import org.blacksoil.shareddto.gitdiff.GitDiffResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GitDiffService {

  private final GitCommandExecutor executor;

  public GitDiffResponse getChangedFiles(String projectDir, String branch) {
    if (!GitDiffValidator.isValid(projectDir, branch)) {
      log.warn("Invalid input: projectDir={}, branch={}", projectDir, branch);
      return new GitDiffResponse(List.of(), List.of());
    }

    List<String> output =
        executor.runGitCommand(projectDir, "diff", "--name-status", "origin/" + branch);

    List<String> addedOrModified = new ArrayList<>();
    List<String> deleted = new ArrayList<>();

    for (String line : output) {
      log.info("git diff: {}", line);
      if (line.startsWith("A") || line.startsWith("M")) {
        addedOrModified.add(line.substring(1).trim());
      } else if (line.startsWith("D")) {
        deleted.add(line.substring(1).trim());
      }
    }

    return new GitDiffResponse(addedOrModified, deleted);
  }
}
