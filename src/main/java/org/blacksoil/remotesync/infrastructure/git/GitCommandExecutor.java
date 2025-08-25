package org.blacksoil.remotesync.infrastructure.git;

import java.util.List;

public interface GitCommandExecutor {
  List<String> runGitCommand(String directory, String... args);
}
