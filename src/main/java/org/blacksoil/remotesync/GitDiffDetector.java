package org.blacksoil.remotesync;

import com.intellij.openapi.diagnostic.Logger;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.blacksoil.remotesync.validator.GitDiffValidator;

public class GitDiffDetector {
  private static final Logger LOG = Logger.getInstance(GitDiffDetector.class);

  public static DiffResult getChangedFiles(String projectDir, String branch) {
    if (GitDiffValidator.isValid(projectDir, branch)) {
      LOG.warn(String.format("Invalid input: projectDir=%s, branch=%s", projectDir, branch));
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
        LOG.warn(
            MessageFormat.format(
                "Git command exited with code {0}: {1}", exitCode, String.join(" ", command)));
      }

    } catch (Exception e) {
      LOG.error(MessageFormat.format("Git command failed: {0}", String.join(" ", args), e));
    }

    return result;
  }

  public record DiffResult(List<String> addedOrModified, List<String> deleted) {}
}
