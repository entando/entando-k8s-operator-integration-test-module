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

package org.entando.kubernetes.model;

import java.util.Locale;
import java.util.Map.Entry;
import java.util.Optional;

public class TestConfig {

    private static final String ENTANDO_TEST_NAMESPACE_OVERRIDE = "entando.test.namespace.override";
    private static final String ENTANDO_TEST_NAME_SUFFIX = "entando.test.name.suffix";

    static Optional<String> getKubernetesUsername() {
        return lookupProperty("entando.kubernetes.username");
    }

    static Optional<String> getKubernetesPassword() {
        return lookupProperty("entando.kubernetes.password");
    }

    static Optional<String> getKubernetesMasterUrl() {
        return lookupProperty("entando.kubernetes.master.url");
    }

    static String calculateName(String baseName) {
        return baseName + getTestNameSuffix().map(s -> "-" + s).orElse("");
    }

    static String calculateNameSpace(String baseName) {
        return calculateName(getTestNamespaceOverride().orElse(baseName));
    }

    static Optional<String> getTestNamespaceOverride() {
        return lookupProperty(ENTANDO_TEST_NAMESPACE_OVERRIDE);
    }

    static Optional<String> getTestNameSuffix() {
        return lookupProperty(ENTANDO_TEST_NAME_SUFFIX);
    }

    static Optional<String> lookupProperty(String name) {
        Optional<String> fromEnv = System.getenv().entrySet().stream()
                .filter(entry -> isMatch(name, entry))
                .map(Entry::getValue)
                .findFirst();
        if (fromEnv.isPresent()) {
            return fromEnv;
        } else {
            return System.getProperties().entrySet().stream()
                    .filter(entry -> isMatch(name, entry))
                    .map(Entry::getValue)
                    .map(String.class::cast)
                    .findFirst();
        }
    }

    static boolean isMatch(String n, Entry<?, ?> entry) {
        if (entry.getValue() == null || ((String) entry.getValue()).trim().isEmpty()) {
            return false;
        }
        String name = n.toLowerCase(Locale.getDefault());
        String key = ((String) entry.getKey()).toLowerCase(Locale.getDefault());
        return name.equals(key) || snakeCaseOf(name).equals(key);
    }

    static String snakeCaseOf(String in) {
        return in.replace("-", "_").replace(".", "_");
    }

}
