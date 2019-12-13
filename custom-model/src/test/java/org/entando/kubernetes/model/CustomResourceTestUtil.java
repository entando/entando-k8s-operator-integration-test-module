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

public interface CustomResourceTestUtil {

    @SuppressWarnings("unchecked")
    default void prepareNamespace(CustomResourceOperationsImpl oper, String namespace) {
        if (getClient().namespaces().withName(namespace).get() == null) {
            getClient().namespaces().createNew().withNewMetadata().withName(namespace).addToLabels("testKind", "end-to-end").endMetadata()
                    .done();
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

}
