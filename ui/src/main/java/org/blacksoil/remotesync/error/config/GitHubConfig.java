package org.blacksoil.remotesync.error.config;

public record GitHubConfig(String token, String owner, String repo) {
  public static GitHubConfig fromSystemProperties() {
    String token = System.getProperty("GITHUB_TOKEN");
    String owner = System.getProperty("GITHUB_REPO_OWNER");
    String repo = System.getProperty("GITHUB_REPO_NAME");

    if (token == null || token.isBlank()) {
      throw new IllegalStateException("GITHUB_TOKEN is not set.");
    }
    if (owner == null || repo == null) {
      throw new IllegalStateException("GITHUB_REPO_OWNER or GITHUB_REPO_NAME is not set.");
    }

    return new GitHubConfig(token, owner, repo);
  }
}
