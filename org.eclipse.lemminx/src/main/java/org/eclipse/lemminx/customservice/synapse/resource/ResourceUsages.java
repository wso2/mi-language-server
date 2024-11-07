package org.eclipse.lemminx.customservice.synapse.resource;

import java.util.ArrayList;
import java.util.List;

public class ResourceUsages {

    List<String> resourceUsedFiles;

    public ResourceUsages() {

        this.resourceUsedFiles = new ArrayList<>();
    }

    public void addResourceUsedFile(String resourceUsedFile) {

        resourceUsedFiles.add(resourceUsedFile);
    }

    public List<String> getResourceUsedFiles() {

        return resourceUsedFiles;
    }
}
