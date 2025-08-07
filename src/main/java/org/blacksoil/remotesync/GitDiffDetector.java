package org.blacksoil.remotesync;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.blacksoil.remotesync.validator.InputValidator;

@Slf4j
public class GitDiffDetector {

  public record DiffResult(List<String> addedOrModified, List<String> deleted) {}

  public static DiffResult getChangedFiles(String projectDir, String branch) {
    if (InputValidator.isValid(projectDir, branch)) {
      log.warn("Invalid input: projectDir='{}', branch='{}'", projectDir, branch);
      return new DiffResult(List.of(), List.of());
    }

    List<String> addedOrModified =
        runGitCommand(projectDir, "diff", "--name-only", "origin/" + branch);
    List<String> deleted =
        runGitCommand(projectDir, "diff", "--name-only", "--diff-filter=D", "origin/" + branch);

    return new DiffResult(addedOrModified, deleted);
  }

  private static List<String> runGitCommand(String directory, String... args) {
    List<String> result = new ArrayList<>();

    try {
      List<String> command = new ArrayList<>();
      command.add("git");
      Collections.addAll(command, args);

      ProcessBuilder builder = new ProcessBuilder(command);
      builder.directory(new File(directory));
      builder.redirectErrorStream(true);

      Process process = builder.start();

      try (BufferedReader reader =
          new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
          result.add(line.trim());
        }
      }

      int exitCode = process.waitFor();
      if (exitCode != 0) {
        log.warn("Git command exited with code {}: {}", exitCode, String.join(" ", command));
      }

    } catch (Exception e) {
      log.error("Git command failed: {}", String.join(" ", args), e);
    }

    return result;
  }
}
