package org.eclipse.lemminx.customservice.synapse.parser.config;

public class ConfigurableEntry {

    private String name;
    private String type;

    public ConfigurableEntry(String name, String type) {

        this.name = name;
        this.type = type;
    }

    public String getName() {

        return name;
    }

    public String getType() {

        return type;
    }
}
