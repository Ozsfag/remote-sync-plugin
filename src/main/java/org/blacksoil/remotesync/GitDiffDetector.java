package org.blacksoil.remotesync;

import com.intellij.openapi.diagnostic.Logger;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GitDiffDetector {

  private static final Logger LOG = Logger.getInstance(GitDiffDetector.class);

  public record DiffResult(List<String> addedOrModified, List<String> deleted) {}

  public static DiffResult getChangedFiles(String projectDir, String branch) {
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
      builder.directory(new java.io.File(directory));
      builder.redirectErrorStream(true);

      Process process = builder.start();
      try (BufferedReader reader =
          new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
          result.add(line.trim());
        }
      }
      process.waitFor();
    } catch (Exception e) {
      LOG.error("Git command failed: " + String.join(" ", args), e);
    }
    return result;
  }
}
