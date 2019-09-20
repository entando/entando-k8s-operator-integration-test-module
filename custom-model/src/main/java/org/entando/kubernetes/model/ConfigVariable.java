package org.entando.kubernetes.model;

public class ConfigVariable {

    private final String configKey;
    private final String environmentVariable;

    public ConfigVariable(String configKey, String environmentVariable) {
        this.configKey = configKey;
        this.environmentVariable = environmentVariable;
    }

    public String getConfigKey() {
        return configKey;
    }

    public String getEnvironmentVariable() {
        return environmentVariable;
    }
}
