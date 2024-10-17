package com.serch.server;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ChangelogGenerator {
    public static void main(String[] args) {
        generateChangelog(args);
    }

    public static void generateChangelog(String[] args) {
        String baseCommit = (args.length > 0) ? args[0] : "HEAD~1";
        String headCommit = (args.length > 1) ? args[1] : "HEAD";

        Changelog changelog = new Changelog();

        // Get the changes
        List<String> changes = getGitDiff(baseCommit, headCommit);
        for (String change : changes) {
            String[] parts = change.split("\t");
            if (parts.length == 2) {
                String status = parts[0];
                String file = parts[1];
                switch (status) {
                    case "A":
                        changelog.getAdded().add(file);
                        break;
                    case "M":
                        changelog.getModified().add(file);
                        break;
                    case "D":
                        changelog.getDeleted().add(file);
                        break;
                }
            }
        }

        // Prepare changelog content
        String changelogContent = prepareChangelogContent(
                changelog,
                getCommitMessages(baseCommit, headCommit),
                getVersionFromPom(false),
                getVersionFromPom(true)
        );

        // Write to CHANGELOG.md
        writeChangelogToFile(changelogContent);
    }

    private static List<String> getGitDiff(String baseCommit, String headCommit) {
        return executeGitCommand(String.format("git diff --name-status %s %s", baseCommit, headCommit));
    }

    private static String getCommitMessages(String baseCommit, String headCommit) {
        List<String> messages = executeGitCommand(String.format("git log --oneline %s..%s", baseCommit, headCommit));
        return String.join("\n", messages);
    }

    private static String getVersionFromPom(boolean isParent) {
        // Read version from pom.xml (Assuming it's in the same directory)
        try {
            File pomFile = new File("pom.xml");
            BufferedReader reader = new BufferedReader(new InputStreamReader(new java.io.FileInputStream(pomFile)));
            String line;
            boolean isParentLevel = true;
            while ((line = reader.readLine()) != null) {
                if(line.contains("</parent>")) {
                    isParentLevel = false;
                }

                // Look for the version of the project, not the parent
                if(isParent) {
                    if (line.contains("<version>") && isParentLevel) {
                        return line.replaceAll("<.*?>", "").trim(); // Strip XML tags
                    }
                } else {
                    if (line.contains("<version>") && !isParentLevel) {
                        return line.replaceAll("<.*?>", "").trim(); // Strip XML tags
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading pom.xml: " + e.getMessage());
        }
        return "unknown";
    }

    private static List<String> executeGitCommand(String command) {
        List<String> output = new ArrayList<>();
        ProcessBuilder processBuilder = new ProcessBuilder();

        // Split the command into arguments for ProcessBuilder
        String[] commandParts = command.split(" ");
        processBuilder.command(commandParts);

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.add(line);
            }
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            System.err.println("Error executing command: " + e.getMessage());
        }
        return output;
    }

    private static String prepareChangelogContent(Changelog changelog, String commitMessages, String version, String springVersion) {
        StringBuilder content = new StringBuilder();
        content.append("# Changelog\n\n")
                .append("Version: **").append(version).append("**\n\n")
                .append("SpringBoot Version: **").append(springVersion).append("**\n\n")
                .append("All notable changes to this project will be documented in this file.\n\n")
                .append("## [Unreleased]\n\n");

        // Commit messages section
        if (!commitMessages.isEmpty()) {
            content.append("### ✍️ Commit Messages\n\n");
            for (String message : commitMessages.split("\n")) {
                content.append("* ").append(message).append("\n");
            }
            content.append('\n');
        }

        // Added section
        if (!changelog.getAdded().isEmpty()) {
            content.append("### ✨ Added\n\n");
            for (String file : changelog.getAdded()) {
                content.append("* `").append(file).append("` - This file introduces new functionality or features to enhance the user experience.\n");
            }
            content.append('\n');
        }

        // Modified section
        if (!changelog.getModified().isEmpty()) {
            content.append("### 🛠️ Modified\n\n");
            for (String file : changelog.getModified()) {
                content.append("* `").append(file).append("` - This file has been updated to improve functionality or fix issues, ensuring a better performance.\n");
            }
            content.append('\n');
        }

        // Deleted section
        if (!changelog.getDeleted().isEmpty()) {
            content.append("### ❌ Deleted\n\n");
            for (String file : changelog.getDeleted()) {
                content.append("* `").append(file).append("` - This file has been removed as it was no longer necessary or has been replaced by better alternatives.\n");
            }
            content.append('\n');
        }

        return content.toString().trim() + "\n\n"; // Extra newline at the end
    }

    private static void writeChangelogToFile(String content) {
        try (FileWriter writer = new FileWriter("CHANGELOG.md")) {
            writer.write(content);
            System.out.println("CHANGELOG.md has been updated successfully!");
        } catch (IOException e) {
            System.err.println("Error writing CHANGELOG.md: " + e.getMessage());
        }
    }

    // Changelog class
    @Getter
    private static class Changelog {
        private final List<String> added = new ArrayList<>();
        private final List<String> modified = new ArrayList<>();
        private final List<String> deleted = new ArrayList<>();

    }
}