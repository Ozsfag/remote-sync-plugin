package org.blacksoil.remotesync.backend.diff.command;

import java.util.List;

public interface GitCommandExecutor {
  List<String> runGitCommand(String directory, String... args);
}
