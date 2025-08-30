package org.blacksoil.gitdiff.component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DefaultGitCommandExecutor implements GitCommandExecutor {

  @Override
  public List<String> runGitCommand(String directory, String... args) {
    List<String> output = new ArrayList<>();
    List<String> command = new ArrayList<>();
    command.add("git");
    command.addAll(Arrays.asList(args));

    log.info("Running git command: {} [dir={}]", String.join(" ", command), directory);

    try {
      ProcessBuilder builder = new ProcessBuilder(command);
      builder.directory(new java.io.File(directory));
      builder.redirectErrorStream(true); // stdout + stderr
      Process process = builder.start();

      try (BufferedReader reader =
          new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        String line;
        while ((line = reader.readLine()) != null) {
          log.info("git output: {}", line);
          output.add(line);
        }
      }

      int exitCode = process.waitFor();
      if (exitCode != 0) {
        log.warn("Git command exited with code {}", exitCode);
      }

    } catch (Exception e) {
      log.error("Failed to run git command", e);
    }

    return output;
  }
}
