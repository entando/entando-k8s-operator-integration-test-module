/*
 *
 * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 *  This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 */

package org.entando.kubernetes.model.plugin;

import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.Locale;

@RegisterForReflection
public enum PluginSecurityLevel {
    STRICT, LENIENT;

    @JsonCreator
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

    @JsonValue
    public String toName() {
        return name().toLowerCase(Locale.getDefault());
    }
}
