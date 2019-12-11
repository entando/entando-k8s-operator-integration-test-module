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

import static org.awaitility.Awaitility.await;

import io.fabric8.kubernetes.client.CustomResourceList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.internal.CustomResourceOperationsImpl;
import java.time.Duration;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Optional;

public interface CustomResourceTestUtil {

    @SuppressWarnings("unchecked")
    default void prepareNamespace(CustomResourceOperationsImpl oper, String namespace) {
        if (getClient().namespaces().withName(namespace).get() == null) {
            getClient().namespaces().createNew().withNewMetadata().withName(namespace).endMetadata().done();
        } else {
            await().atMost(Duration.ofMinutes(2)).until(() -> {
                if (((CustomResourceList) oper.inNamespace(namespace).list()).getItems().size() > 0) {
                    try {
                        oper.inNamespace(namespace)
                                .delete(((CustomResourceList) oper.inNamespace(namespace).list()).getItems().get(0));
                        return false;
                    } catch (IndexOutOfBoundsException e) {
                        return true;
                    }
                } else {
                    return true;
                }
            });
        }
    }

    KubernetesClient getClient();

    String ENTANDO_TEST_NAMESPACE_OVERRIDE = "entando.test.namespace.override";
    String ENTANDO_TEST_NAME_SUFFIX = "entando.test.name.suffix";

    default Optional<String> getKubernetesUsername() {
        return lookupProperty("entando.kubernetes.username");
    }

    default Optional<String> getKubernetesPassword() {
        return lookupProperty("entando.kubernetes.password");
    }

    default Optional<String> getKubernetesMasterUrl() {
        return lookupProperty("entando.kubernetes.master.url");
    }

    default String calculateName(String baseName) {
        return baseName + getTestNameSuffix().map(s -> "-" + s).orElse("");
    }

    default String calculateNameSpace(String baseName) {
        return calculateName(getTestNamespaceOverride().orElse(baseName));
    }

    default Optional<String> getTestNamespaceOverride() {
        return lookupProperty(ENTANDO_TEST_NAMESPACE_OVERRIDE);
    }

    default Optional<String> getTestNameSuffix() {
        return lookupProperty(ENTANDO_TEST_NAME_SUFFIX);
    }

    default Optional<String> lookupProperty(String name) {
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

    default boolean isMatch(String n, Entry<?, ?> entry) {
        if (entry.getValue() == null || ((String) entry.getValue()).trim().isEmpty()) {
            return false;
        }
        String name = n.toLowerCase(Locale.getDefault());
        String key = ((String) entry.getKey()).toLowerCase(Locale.getDefault());
        return name.equals(key) || snakeCaseOf(name).equals(key);
    }

    default String snakeCaseOf(String in) {
        return in.replace("-", "_").replace(".", "_");
    }

}
