package org.blacksoil.remotesync;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GitDiffDetector {

    public static List<String> getChangedFiles(String projectDir, String branch) {
        List<String> changedFiles = new ArrayList<>();
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "git", "diff", "--name-only", "origin/" + branch
            );
            builder.directory(new java.io.File(projectDir));
            builder.redirectErrorStream(true);

            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                changedFiles.add(line.trim());
            }

            process.waitFor();
        } catch (Exception e) {
            System.err.println("Error detecting git changes: " + e.getMessage());
        }

        return changedFiles;
    }

}
