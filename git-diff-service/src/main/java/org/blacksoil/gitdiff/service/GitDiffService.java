package org.blacksoil.gitdiff.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.blacksoil.dto.GitDiffResponse;
import org.blacksoil.gitdiff.component.GitCommandExecutor;
import org.blacksoil.gitdiff.validator.GitDiffValidator;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GitDiffService {

  private final GitCommandExecutor executor;

  public GitDiffResponse getChangedFiles(String projectDir, String branch) {
    return getChangedFiles(projectDir, branch, executor);
  }

  public GitDiffResponse getChangedFiles(
      String projectDir, String branch, GitCommandExecutor customExecutor) {
    if (!GitDiffValidator.isValid(projectDir, branch)) {
      log.warn("Invalid input: projectDir={}, branch={}", projectDir, branch);
      return new GitDiffResponse(List.of(), List.of());
    }

    List<String> output =
        customExecutor.runGitCommand(projectDir, "diff", "--name-status", "origin/" + branch);

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
