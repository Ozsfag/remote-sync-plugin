package org.blacksoil.gitdiff.component;

import java.util.List;

public interface GitCommandExecutor {
  List<String> runGitCommand(String dir, String... args);
}
