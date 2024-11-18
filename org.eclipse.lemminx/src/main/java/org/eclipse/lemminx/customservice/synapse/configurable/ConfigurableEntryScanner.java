package org.eclipse.lemminx.customservice.synapse.configurable;

import org.eclipse.lemminx.customservice.synapse.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ConfigurableEntryScanner {

    public static List<ConfigurableEntry> scanConfigurableEntries(String projectPath)
            throws IOException {

        String configurableFilePath = getConfigurableFilePath(projectPath);
        File configurableFile = new File(configurableFilePath);
        String configurableFileContent = Utils.readFile(configurableFile);
        return extractConfigurableFileContent(configurableFileContent);
    }

    private static List<ConfigurableEntry> extractConfigurableFileContent(String content) {
        List<ConfigurableEntry> result = new ArrayList<>();
        String[] lines = content.split("\n");
        for (String line : lines) {
            String[] parts = line.split(":");
            if (parts.length == 2) {
                String configurableName = parts[0];
                String configurableType = parts[1];
                ConfigurableEntry configurableEntry = new ConfigurableEntry(configurableName, configurableType);
                result.add(configurableEntry);
            }
        }
        return result;
    }

    private static String getConfigurableFilePath(String projectPath) {

        if (projectPath != null) {
            Path configurableFilePath = Path.of(projectPath, "src", "main", "wso2mi", "resources", "conf", "config.properties");
            return configurableFilePath.toString();
        }
        return "";
    }
}
