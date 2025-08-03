package org.blacksoil.remotesync;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GitDiffDetector {

  public static class DiffResult {
    public List<String> addedOrModified = new ArrayList<>();
    public List<String> deleted = new ArrayList<>();
  }

  public static DiffResult getChangedFiles(String projectDir, String branch) {
    DiffResult result = new DiffResult();
    try {
      ProcessBuilder builder = new ProcessBuilder(
              "git", "diff", "--name-status", "origin/" + branch
      );
      builder.directory(new java.io.File(projectDir));
      builder.redirectErrorStream(true);

      Process process = builder.start();
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

      String line;
      while ((line = reader.readLine()) != null) {
        String[] parts = line.trim().split("\t");
        if (parts.length == 2) {
          String status = parts[0];
          String file = parts[1];

          switch (status) {
            case "A":
            case "M":
              result.addedOrModified.add(file);
              break;
            case "D":
              result.deleted.add(file);
              break;
            // можно добавить "R", "C" — rename/copy если потребуется
          }
        }
      }

      process.waitFor();
    } catch (Exception e) {
      System.err.println("Error detecting git changes: " + e.getMessage());
    }

    return result;
  }
}
