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

import static java.lang.String.format;
import static java.lang.Thread.sleep;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.kubernetes.client.CustomResourceList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.internal.CustomResourceOperationsImpl;
import io.fabric8.kubernetes.internal.KubernetesDeserializer;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.entando.kubernetes.model.app.EntandoApp;
import org.entando.kubernetes.model.externaldatabase.EntandoDatabaseService;
import org.entando.kubernetes.model.infrastructure.EntandoClusterInfrastructure;
import org.entando.kubernetes.model.keycloakserver.EntandoKeycloakServer;
import org.entando.kubernetes.model.link.EntandoAppPluginLink;
import org.entando.kubernetes.model.plugin.EntandoPlugin;

public class EntandoCustomResourceResolver<R extends EntandoCustomResource, L extends CustomResourceList<R>, D extends
        DoneableEntandoCustomResource<D, R>> {

    private static final Logger LOGGER = Logger.getLogger(EntandoCustomResourceResolver.class.getName());

    static {
        registerCustomKinds();
    }

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

    public static void registerCustomKinds() {
        KubernetesDeserializer.registerCustomKind("entando.org/v1#EntandoApp", EntandoApp.class);
        KubernetesDeserializer.registerCustomKind("entando.org/v1#EntandoPlugin", EntandoPlugin.class);
        KubernetesDeserializer.registerCustomKind("entando.org/v1#EntandoClusterInfrastructure", EntandoClusterInfrastructure.class);
        KubernetesDeserializer.registerCustomKind("entando.org/v1#EntandoKeycloakServer", EntandoKeycloakServer.class);
        KubernetesDeserializer.registerCustomKind("entando.org/v1#EntandoAppPluginLink", EntandoAppPluginLink.class);
        KubernetesDeserializer.registerCustomKind("entando.org/v1#EntandoDatabaseService", EntandoDatabaseService.class);
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
            int count = 0;
            while (notAvailable(oper, client) && count < 100) {
                sleep(100);
                count++;
            }
            if (notAvailable(oper, client)) {
                throw new IllegalStateException("Could not resolve CRD for " + this.customResourceClass);
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
                crd = client.customResourceDefinitions().load(loadYamlFile()).get();
                // see issue https://github.com/fabric8io/kubernetes-client/issues/1486
                crd.getSpec().getValidation().getOpenAPIV3Schema().setDependencies(null);
                client.customResourceDefinitions().create(crd);
            }
            return crd;
        } catch (KubernetesClientException e) {
            if (e.getCode() == HttpURLConnection.HTTP_FORBIDDEN) {
                LOGGER.severe("User does not have permissions to create CRD's. Loading from memory.");
                //The code doesn't have RBAC permission to read the CRD. Let's assume it has already been deployed
                List<HasMetadata> list = client.load(loadYamlFile()).get();
                return (CustomResourceDefinition) list.get(0);
            } else {
                LOGGER.log(Level.SEVERE, "Error", e);
                throw e;
            }
        } catch (RuntimeException e) {
            throw e;
        }
    }

    private InputStream loadYamlFile() {
        InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(yamlFile);
        if (resourceAsStream == null) {
            LOGGER.severe(() -> format("Could not load yaml file: %s", yamlFile));
            throw new IllegalStateException("Could not load yaml file: " + yamlFile);
        }
        return resourceAsStream;
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
