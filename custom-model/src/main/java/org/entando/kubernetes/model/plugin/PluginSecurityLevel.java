package org.entando.kubernetes.model.plugin;

import static java.util.Optional.ofNullable;

import java.util.Locale;

public enum PluginSecurityLevel {
    STRICT, LENIENT;

    public static PluginSecurityLevel forName(String name) {
        try {
            return ofNullable(name).map(PluginSecurityLevel::resolve).orElse(null);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static PluginSecurityLevel resolve(String s) {
        return PluginSecurityLevel.valueOf(s.toUpperCase(Locale.getDefault()));
    }

    public String toName() {
        return name().toLowerCase(Locale.getDefault());
    }
}
