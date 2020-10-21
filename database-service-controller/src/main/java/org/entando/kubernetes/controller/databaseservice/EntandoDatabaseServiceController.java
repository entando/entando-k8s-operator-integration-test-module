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

import io.fabric8.kubernetes.client.KubernetesClient;
import io.quarkus.runtime.StartupEvent;
import java.util.Optional;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.entando.kubernetes.controller.AbstractDbAwareController;
import org.entando.kubernetes.controller.DeployCommand;
import org.entando.kubernetes.controller.SimpleKeycloakClient;
import org.entando.kubernetes.controller.common.CreateExternalServiceCommand;
import org.entando.kubernetes.controller.database.DatabaseDeployable;
import org.entando.kubernetes.controller.database.DatabaseDeploymentResult;
import org.entando.kubernetes.controller.database.DbmsDockerVendorStrategy;
import org.entando.kubernetes.controller.k8sclient.SimpleK8SClient;
import org.entando.kubernetes.model.externaldatabase.EntandoDatabaseService;

public class EntandoDatabaseServiceController extends AbstractDbAwareController<EntandoDatabaseService> {

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
            DatabaseDeployable<EntandoDatabaseService> deployable = new DatabaseDeployable<>(
                    DbmsDockerVendorStrategy.forVendor(newEntandoDatabaseService.getSpec().getDbms()), newEntandoDatabaseService, "db");
            DatabaseDeploymentResult result = new DeployCommand<>(deployable).execute(k8sClient, Optional.ofNullable(keycloakClient));
            k8sClient.entandoResources().updateStatus(newEntandoDatabaseService, result.getStatus());
        } else {
            CreateExternalServiceCommand command = new CreateExternalServiceCommand(newEntandoDatabaseService, "db");
            command.execute(k8sClient);
            k8sClient.entandoResources().updateStatus(newEntandoDatabaseService, command.getStatus());
        }
    }

}
