package org.entando.entando.kubernetes.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.fabric8.zjsonpatch.internal.guava.Strings;
import java.util.Locale;

public enum EntandoDeploymentPhase {
    REQUESTED, STARTED(), SUCCESSFUL(), FAILED();

    @JsonCreator
    public static EntandoDeploymentPhase forValue(String value) {
        if (Strings.isNullOrEmpty(value)) {
            return null;
        }
        return EntandoDeploymentPhase.valueOf(value.toUpperCase(Locale.getDefault()));
    }

    @JsonValue
    public String toValue() {
        return name().toLowerCase(Locale.getDefault());
    }

    public boolean requiresSync() {
        return this != STARTED;
    }
}
