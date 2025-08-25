package org.blacksoil.remotesync.infrastructure.git;

import com.intellij.openapi.diagnostic.Logger;
import java.io.*;
import java.text.MessageFormat;
import java.util.*;

public class DefaultGitCommandExecutor implements GitCommandExecutor {

  private static final Logger LOG = Logger.getInstance(DefaultGitCommandExecutor.class);

  @Override
  public List<String> runGitCommand(String directory, String... args) {
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
      LOG.error(MessageFormat.format("Git command failed: {0}", String.join(" ", args)));
    }

    return result;
  }
}
