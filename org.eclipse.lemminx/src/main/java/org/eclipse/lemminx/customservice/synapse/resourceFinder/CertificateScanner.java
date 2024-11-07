package org.eclipse.lemminx.customservice.synapse.resourceFinder;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CertificateScanner {

    public static List<String> scanCertificates(String projectPath) {

        String certificateDirPath = getCertificateDirPath(projectPath);
        File folder = new File(certificateDirPath);
        File[] files = folder.listFiles();
        List<String> certificateFiles = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && !file.isHidden()) {
                    certificateFiles.add(file.getName());
                }
            }
        }
        return certificateFiles;
    }

    private static String getCertificateDirPath(String projectPath) {

        if (projectPath != null) {
            Path certificateDirPath = Path.of(projectPath, "src", "main", "wso2mi", "resources", "certificates");
            return certificateDirPath.toString();
        }
        return "";
    }
}
