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

package org.entando.kubernetes.controller.databaseservice;

import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.quarkus.runtime.StartupEvent;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.entando.kubernetes.controller.spi.common.DbmsDockerVendorStrategy;
import org.entando.kubernetes.controller.spi.common.EntandoOperatorSpiConfig;
import org.entando.kubernetes.controller.spi.database.DatabaseDeployable;
import org.entando.kubernetes.controller.spi.database.DatabaseDeploymentResult;
import org.entando.kubernetes.controller.support.client.SimpleK8SClient;
import org.entando.kubernetes.controller.support.client.SimpleKeycloakClient;
import org.entando.kubernetes.controller.support.command.CreateExternalServiceCommand;
import org.entando.kubernetes.controller.support.command.DeployCommand;
import org.entando.kubernetes.controller.support.controller.AbstractDbAwareController;
import org.entando.kubernetes.model.externaldatabase.EntandoDatabaseService;
import org.entando.kubernetes.model.externaldatabase.EntandoDatabaseServiceSpec;

public class EntandoDatabaseServiceController extends AbstractDbAwareController<EntandoDatabaseServiceSpec, EntandoDatabaseService> {

    @Inject
    public EntandoDatabaseServiceController(KubernetesClient kubernetesClient) {
        super(kubernetesClient);
    }

    public EntandoDatabaseServiceController(KubernetesClient kubernetesClient, boolean exitAutomatically) {
        super(kubernetesClient, exitAutomatically);
    }

    public EntandoDatabaseServiceController(SimpleK8SClient<?> k8sClient, SimpleKeycloakClient keycloakClient) {
        super(k8sClient, keycloakClient);
    }

    public void onStartup(@Observes StartupEvent event) {
        processCommand();
    }

    @Override
    protected void synchronizeDeploymentState(EntandoDatabaseService newEntandoDatabaseService) {
        if (newEntandoDatabaseService.getSpec().getCreateDeployment().orElse(false)) {
            DatabaseDeployable<EntandoDatabaseServiceSpec> deployable = new DatabaseDeployable<EntandoDatabaseServiceSpec>(
                    DbmsDockerVendorStrategy
                            .forVendor(newEntandoDatabaseService.getSpec().getDbms(), EntandoOperatorSpiConfig.getComplianceMode()),
                    newEntandoDatabaseService, newEntandoDatabaseService.getSpec().getPort().orElse(null)) {
                @Override
                protected String getDatabaseAdminSecretName() {
                    return getCustomResource().getSpec().getSecretName().orElse(super.getDatabaseAdminSecretName());
                }

                @Override
                public List<Secret> getSecrets() {
                    if (getCustomResource().getSpec().getSecretName().isPresent()) {
                        //because it already exists
                        return Collections.emptyList();
                    } else {
                        return super.getSecrets();
                    }
                }

                @Override
                protected String getDatabaseName() {
                    return getCustomResource().getSpec().getDatabaseName().orElse(super.getDatabaseName());
                }
            };
            DatabaseDeploymentResult result = new DeployCommand<>(deployable).execute(k8sClient, Optional.ofNullable(keycloakClient));
            k8sClient.entandoResources().updateStatus(newEntandoDatabaseService, result.getStatus());
        } else {
            CreateExternalServiceCommand command = new CreateExternalServiceCommand(newEntandoDatabaseService);
            command.execute(k8sClient);
            k8sClient.entandoResources().updateStatus(newEntandoDatabaseService, command.getStatus());
        }
    }

}
