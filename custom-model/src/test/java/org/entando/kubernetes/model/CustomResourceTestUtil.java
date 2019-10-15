/*
 *
 *  * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
 *  *
 *  * This library is free software; you can redistribute it and/or modify it under
 *  * the terms of the GNU Lesser General Public License as published by the Free
 *  * Software Foundation; either version 2.1 of the License, or (at your option)
 *  * any later version.
 *  *
 *  * This library is distributed in the hope that it will be useful, but WITHOUT
 *  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 *  * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 *  * details.
 *
 */

package org.entando.kubernetes.model;

import io.fabric8.kubernetes.client.CustomResourceList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.internal.CustomResourceOperationsImpl;

public interface CustomResourceTestUtil {

    default void prepareNamespace(CustomResourceOperationsImpl oper, String namespace) throws InterruptedException {
        if (getClient().namespaces().withName(namespace).get() == null) {
            getClient().namespaces().createNew().withNewMetadata().withName(namespace).endMetadata().done();
        } else {
            while (((CustomResourceList) oper.inNamespace(namespace).list()).getItems().size() > 0) {
                try {
                    oper.inNamespace(namespace)
                            .delete(((CustomResourceList) oper.inNamespace(namespace).list()).getItems().get(0));
                    Thread.sleep(100);
                } catch (IndexOutOfBoundsException e) {
                    return;
                }
            }
        }
    }

    KubernetesClient getClient();
}
