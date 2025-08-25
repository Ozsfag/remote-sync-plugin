package org.blacksoil.remotesync.infrastructure.git;

import com.intellij.openapi.diagnostic.Logger;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefaultGitCommandExecutor implements GitCommandExecutor {

  private static final Logger LOG = Logger.getInstance(DefaultGitCommandExecutor.class);

  @Override
  public List<String> runGitCommand(String directory, String... args) {
    List<String> output = new ArrayList<>();
    List<String> command = new ArrayList<>();
    command.add("git");
    command.addAll(Arrays.asList(args));

    LOG.info("Running git command: " + String.join(" ", command) + " [dir=" + directory + "]");

    try {
      ProcessBuilder builder = new ProcessBuilder(command);
      builder.directory(new java.io.File(directory));
      builder.redirectErrorStream(true); // stdout + stderr
      Process process = builder.start();

      try (BufferedReader reader =
          new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
          LOG.info("git output: " + line);
          output.add(line);
        }
      }

      int exitCode = process.waitFor();
      if (exitCode != 0) {
        LOG.warn("Git command exited with code " + exitCode);
      }

    } catch (Exception e) {
      LOG.error("Failed to run git command", e);
    }

    return output;
  }
}
