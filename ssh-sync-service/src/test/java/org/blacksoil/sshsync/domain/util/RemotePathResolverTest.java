package org.blacksoil.sshsync.domain.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class RemotePathResolverTest {

  @Test
  void blank_defaults_to_home() {
    assertEquals("/home/root/remote-sync", RemotePathResolver.normalize(null, "root"));
    assertEquals("/home/alice/remote-sync", RemotePathResolver.normalize("   ", "alice"));
  }

  @Test
  void strips_quotes_and_git_suffix() {
    assertEquals("/home/u/myrepo", RemotePathResolver.normalize("'myrepo.git'", "u"));
    assertEquals("/home/u/myrepo", RemotePathResolver.normalize("\"myrepo.git\"", "u"));
  }

  @Test
  void http_git_url_extracts_repo() {
    assertEquals(
        "/home/u/repo", RemotePathResolver.normalize("https://github.com/acme/repo.git", "u"));
  }

  @Test
  void ssh_git_url_extracts_repo() {
    assertEquals("/home/u/repo", RemotePathResolver.normalize("git@github.com:acme/repo.git", "u"));
  }

  @Test
  void tilde_prefixed() {
    assertEquals("/home/alice/project", RemotePathResolver.normalize("~/project", "alice"));
  }

  @Test
  void bare_name_is_under_home() {
    assertEquals("/home/alice/app", RemotePathResolver.normalize("app", "alice"));
  }

  @Test
  void absolute_kept() {
    assertEquals("/opt/app", RemotePathResolver.normalize("/opt/app", "u"));
  }
}
