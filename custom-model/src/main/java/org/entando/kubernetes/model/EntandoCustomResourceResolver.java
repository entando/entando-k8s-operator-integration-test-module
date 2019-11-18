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

import static java.lang.Thread.sleep;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.kubernetes.client.CustomResourceList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.internal.CustomResourceOperationsImpl;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.util.List;

public class EntandoCustomResourceResolver<R extends EntandoCustomResource, L extends CustomResourceList<R>, D extends
        DoneableEntandoCustomResource<D, R>> {

    private final String crdName;
    private final Class<R> customResourceClass;
    private final Class<L> customResourceListClass;
    private final Class<D> doneableCustomResourceClass;
    private final String yamlFile;
    private CustomResourceDefinition customResourceDefinition;

    public EntandoCustomResourceResolver(Class<R> customResourceClass, Class<L> listCass, Class<D> doneableClass) {
        try {
            R r = customResourceClass.getConstructor().newInstance();
            this.yamlFile = "crd/" + r.getKind() + "CRD.yaml";
            this.crdName = r.getDefinitionName();
            this.customResourceClass = customResourceClass;
            this.customResourceListClass = listCass;
            this.doneableCustomResourceClass = doneableClass;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    public CustomResourceOperationsImpl<R, L, D> resolveOperation(KubernetesClient client) {
        try {
            synchronized (this) {
                if (this.customResourceDefinition == null) {
                    this.customResourceDefinition = loadCrd(client);
                }
            }
            CustomResourceOperationsImpl<R, L, D> oper = (CustomResourceOperationsImpl<R, L, D>) client
                    .customResources(customResourceDefinition, customResourceClass, customResourceListClass, doneableCustomResourceClass);
            while (notAvailable(oper, client)) {
                sleep(100);
            }
            return oper;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }

    private CustomResourceDefinition loadCrd(KubernetesClient client) {
        try {
            CustomResourceDefinition crd = client.customResourceDefinitions().withName(crdName).get();
            if (crd == null) {
                List<HasMetadata> list = client.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(yamlFile)).get();
                crd = (CustomResourceDefinition) list.get(0);
                // see issue https://github.com/fabric8io/kubernetes-client/issues/1486
                crd.getSpec().getValidation().getOpenAPIV3Schema().setDependencies(null);
                client.customResourceDefinitions().create(crd);
            }
            return crd;
        } catch (KubernetesClientException e) {
            if (e.getCode() == HttpURLConnection.HTTP_FORBIDDEN) {
                //The code doesn't have RBAC permission to read the CRD. Let's assume it has already been deployed
                List<HasMetadata> list = client.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(yamlFile)).get();
                return (CustomResourceDefinition) list.get(0);
            } else {
                throw e;
            }
        }
    }

    private boolean notAvailable(CustomResourceOperationsImpl<R, L, D> oper, KubernetesClient client) {
        //Sometimes it takes a couple of seconds after registration for the resource to become available.
        try {
            String namespaceToUse = client.getNamespace();
            if (isBlank(namespaceToUse) || client.namespaces().withName(namespaceToUse).get() == null) {
                //Only needed to wait until the resource is available in tests. We can assume the test user has access to the default
                // namespace
                namespaceToUse = "default";
            }
            oper.inNamespace(namespaceToUse).list();
            return false;
        } catch (KubernetesClientException e) {
            return e.getCode() == HttpURLConnection.HTTP_NOT_FOUND;
        }
    }

    private boolean isBlank(String namespaceToUse) {
        return namespaceToUse == null || namespaceToUse.isEmpty();
    }
}
