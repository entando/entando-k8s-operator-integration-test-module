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

package org.entando.kubernetes.controller.app;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.extensions.Ingress;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.entando.kubernetes.controller.KeycloakConnectionConfig;
import org.entando.kubernetes.controller.KubeUtils;
import org.entando.kubernetes.controller.common.InfrastructureConfig;
import org.entando.kubernetes.controller.database.DatabaseServiceResult;
import org.entando.kubernetes.controller.spi.DbAwareDeployable;
import org.entando.kubernetes.controller.spi.DeployableContainer;
import org.entando.kubernetes.controller.spi.PublicIngressingDeployable;
import org.entando.kubernetes.model.DbmsVendor;
import org.entando.kubernetes.model.app.EntandoApp;
import org.entando.kubernetes.model.app.EntandoAppSpec;

public class EntandoAppServerDeployable implements PublicIngressingDeployable<EntandoAppDeploymentResult, EntandoAppSpec>,
        DbAwareDeployable {

    /**
     * The operating system level id of the default user in the EAP and Wildfly base images. Was determined to be 185 running the 'id'
     * command in the entando/entando-eap72-clusted-base image or entando/entando-wildfly17-base image
     */
    public static final long DEFAULT_USERID_IN_JBOSS_BASE_IMAGES = 185L;
    private final EntandoApp entandoApp;
    private final List<DeployableContainer> containers;
    private final DatabaseServiceResult databaseServiceResult;
    private final KeycloakConnectionConfig keycloakConnectionConfig;

    public EntandoAppServerDeployable(EntandoApp entandoApp,
            KeycloakConnectionConfig keycloakConnectionConfig,
            InfrastructureConfig infrastructureConfig,
            DatabaseServiceResult databaseServiceResult) {
        this.entandoApp = entandoApp;
        this.databaseServiceResult = databaseServiceResult;
        this.containers = Arrays.asList(new EntandoAppDeployableContainer(entandoApp, keycloakConnectionConfig),
                new ComponentManagerDeployableContainer(entandoApp, keycloakConnectionConfig, infrastructureConfig),
                new AppBuilderDeployableContainer(entandoApp)
        );
        this.keycloakConnectionConfig = keycloakConnectionConfig;
    }

    @Override
    public Optional<Long> getFileSystemUserAndGroupId() {
        return Optional.of(DEFAULT_USERID_IN_JBOSS_BASE_IMAGES);
    }

    @Override
    public boolean hasContainersExpectingSchemas() {
        return entandoApp.getSpec().getDbms().map(v -> v != DbmsVendor.NONE && v != DbmsVendor.EMBEDDED).orElse(false);
    }

    @Override
    public int getReplicas() {
        return entandoApp.getSpec().getReplicas().orElse(1);
    }

    @Override
    public List<DeployableContainer> getContainers() {
        return containers;
    }

    @Override
    public DatabaseServiceResult getDatabaseServiceResult() {
        return databaseServiceResult;
    }

    @Override
    public String getNameQualifier() {
        return KubeUtils.DEFAULT_SERVER_QUALIFIER;
    }

    @Override
    public EntandoApp getCustomResource() {
        return entandoApp;
    }

    @Override
    public EntandoAppDeploymentResult createResult(Deployment deployment, Service service, Ingress ingress, Pod pod) {
        return new EntandoAppDeploymentResult(pod, service, ingress);
    }

    @Override
    public String getIngressName() {
        return entandoApp.getMetadata().getName() + "-" + KubeUtils.DEFAULT_INGRESS_SUFFIX;
    }

    @Override
    public String getIngressNamespace() {
        return entandoApp.getMetadata().getNamespace();
    }

    @Override
    public KeycloakConnectionConfig getKeycloakDeploymentResult() {
        return keycloakConnectionConfig;
    }
}
