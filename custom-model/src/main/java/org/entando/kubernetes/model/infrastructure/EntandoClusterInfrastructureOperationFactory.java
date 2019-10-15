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

package org.entando.kubernetes.model.infrastructure;

import static java.lang.Thread.sleep;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.internal.CustomResourceOperationsImpl;
import java.util.List;

public final class EntandoClusterInfrastructureOperationFactory {

    private static final int NOT_FOUND = 404;
    private static CustomResourceDefinition entandoInfrastructureCrd;

    private EntandoClusterInfrastructureOperationFactory() {
    }

    public static CustomResourceOperationsImpl<EntandoClusterInfrastructure, EntandoClusterInfrastructureList,
            DoneableEntandoClusterInfrastructure> produceAllEntandoClusterInfrastructures(
            KubernetesClient client) throws InterruptedException {
        synchronized (EntandoClusterInfrastructureOperationFactory.class) {
            if (entandoInfrastructureCrd == null) {
                entandoInfrastructureCrd = client.customResourceDefinitions().withName(EntandoClusterInfrastructure.CRD_NAME).get();
                if (entandoInfrastructureCrd == null) {
                    List<HasMetadata> list = client.load(Thread.currentThread().getContextClassLoader()
                            .getResourceAsStream("crd/EntandoClusterInfrastructureCRD.yaml")).get();
                    entandoInfrastructureCrd = (CustomResourceDefinition) list.get(0);
                    // see issue https://github.com/fabric8io/kubernetes-client/issues/1486
                    entandoInfrastructureCrd.getSpec().getValidation().getOpenAPIV3Schema().setDependencies(null);
                    client.customResourceDefinitions().create(entandoInfrastructureCrd);
                }
            }
        }
        CustomResourceOperationsImpl<EntandoClusterInfrastructure, EntandoClusterInfrastructureList,
                DoneableEntandoClusterInfrastructure>
                oper = (CustomResourceOperationsImpl<EntandoClusterInfrastructure, EntandoClusterInfrastructureList,
                DoneableEntandoClusterInfrastructure>) client
                .customResources(entandoInfrastructureCrd, EntandoClusterInfrastructure.class, EntandoClusterInfrastructureList.class,
                        DoneableEntandoClusterInfrastructure.class);
        while (notAvailable(oper)) {
            sleep(100);
        }
        return oper;
    }

    private static boolean notAvailable(
            CustomResourceOperationsImpl<EntandoClusterInfrastructure, EntandoClusterInfrastructureList,
                    DoneableEntandoClusterInfrastructure> oper) {
        try {
            oper.inNamespace("default").list().getItems().size();
            return false;
        } catch (KubernetesClientException e) {
            return e.getCode() == NOT_FOUND;
        }
    }

}
