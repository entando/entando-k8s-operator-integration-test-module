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

import static java.util.Optional.ofNullable;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.entando.kubernetes.controller.spi.client.KubernetesClientForControllers;
import org.entando.kubernetes.controller.spi.command.CommandStream;
import org.entando.kubernetes.controller.spi.command.SerializingDeployCommand;
import org.entando.kubernetes.controller.spi.common.ResourceUtils;
import org.entando.kubernetes.controller.spi.container.ProvidedDatabaseCapability;
import org.entando.kubernetes.model.capability.CapabilityProvisioningStrategy;
import org.entando.kubernetes.model.capability.CapabilityRequirement;
import org.entando.kubernetes.model.capability.CapabilityScope;
import org.entando.kubernetes.model.capability.ExternallyProvidedService;
import org.entando.kubernetes.model.capability.NestedCapabilityRequirementFluent;
import org.entando.kubernetes.model.capability.ProvidedCapability;
import org.entando.kubernetes.model.capability.ProvidedCapabilityBuilder;
import org.entando.kubernetes.model.capability.StandardCapability;
import org.entando.kubernetes.model.capability.StandardCapabilityImplementation;
import org.entando.kubernetes.model.common.DbmsVendor;
import org.entando.kubernetes.model.common.EntandoCustomResource;
import org.entando.kubernetes.model.common.EntandoDeploymentPhase;
import org.entando.kubernetes.model.externaldatabase.EntandoDatabaseService;
import org.entando.kubernetes.model.externaldatabase.EntandoDatabaseServiceBuilder;
import org.entando.kubernetes.model.externaldatabase.EntandoDatabaseServiceSpec;
import picocli.CommandLine;

@CommandLine.Command()
public class EntandoDatabaseServiceController implements Runnable {

    private final Logger logger = Logger.getLogger(EntandoDatabaseServiceController.class.getName());
    private final KubernetesClientForControllers k8sClient;
    private final SerializingDeployCommand serializingDeployCommand;
    private static final Collection<Class<? extends EntandoCustomResource>> SUPPORTED_RESOURCE_KINDS = Arrays
            .asList(EntandoDatabaseService.class, ProvidedCapability.class);
    private EntandoDatabaseService entandoDatabaseService;
    private ProvidedCapability providedCapability;

    @Inject
    public EntandoDatabaseServiceController(KubernetesClientForControllers k8sClient, CommandStream commandStream) {
        this.k8sClient = k8sClient;
        this.serializingDeployCommand = new SerializingDeployCommand(k8sClient, commandStream);
    }

    @Override
    public void run() {
        EntandoCustomResource resourceToProcess = k8sClient.resolveCustomResourceToProcess(SUPPORTED_RESOURCE_KINDS);
        k8sClient.updatePhase(resourceToProcess, EntandoDeploymentPhase.STARTED);
        //No need to update the resource being synced to. It will be ignored by ControllerCoordinator
        try {
            if (resourceToProcess instanceof EntandoDatabaseService) {
                //This event originated from the original EntandoDatabaseService, NOT a capability requirement expressed by means of a
                // ProvidedCapability
                //The ProvidedCapability is to be owned by the implementing CustomResource and will therefore be ignored by
                // ControllerCoordinator
                this.entandoDatabaseService = (EntandoDatabaseService) resourceToProcess;
                this.providedCapability = syncFromImplementingResourceToCapability(this.entandoDatabaseService);
                this.k8sClient.createOrPatchEntandoResource(this.providedCapability);
            } else {
                //This event originated from the capability requirement, and we need to keep the implementing CustomResource in sync
                //The implementing CustomResource is to be owned by the ProvidedCapability and will therefore be ignored by
                // ControllerCoordinator
                this.providedCapability = (ProvidedCapability) resourceToProcess;
                this.entandoDatabaseService = syncFromCapabilityToImplementingCustomResource(this.providedCapability);
                this.k8sClient.createOrPatchEntandoResource(this.entandoDatabaseService);
            }
            DatabaseServiceDeployable deployable = new DatabaseServiceDeployable(entandoDatabaseService);
            DatabaseDeploymentResult result = serializingDeployCommand.processDeployable(deployable);
            k8sClient.updateStatus(entandoDatabaseService, result.getStatus());
            k8sClient.updateStatus(providedCapability, result.getStatus());
            k8sClient.updatePhase(entandoDatabaseService, EntandoDeploymentPhase.SUCCESSFUL);
            k8sClient.updatePhase(providedCapability, EntandoDeploymentPhase.SUCCESSFUL);
        } catch (Exception e) {
            e.printStackTrace();
            k8sClient.deploymentFailed(entandoDatabaseService, e);
            k8sClient.deploymentFailed(providedCapability, e);
            throw new CommandLine.ExecutionException(new CommandLine(this), e.getMessage());
        }
    }

    private EntandoDatabaseService syncFromCapabilityToImplementingCustomResource(ProvidedCapability providedCapability) {
        final EntandoDatabaseService entandoDatabaseServiceToSyncTo = new EntandoDatabaseServiceBuilder(
                Objects.requireNonNullElseGet(k8sClient
                                .load(EntandoDatabaseService.class, providedCapability.getMetadata().getNamespace(),
                                        providedCapability.getMetadata().getName()),
                        () -> new EntandoDatabaseService(new EntandoDatabaseServiceSpec())))

                .editMetadata()
                .withNamespace(providedCapability.getMetadata().getNamespace())
                .withName(providedCapability.getMetadata().getName())
                .withLabels(providedCapability.getMetadata().getLabels())
                .endMetadata()
                .editSpec()
                .withDbms(DbmsVendor.valueOf(
                        providedCapability.getSpec().getImplementation().orElse(StandardCapabilityImplementation.POSTGRESQL).name()))
                .withCreateDeployment(
                        providedCapability.getSpec().getProvisioningStrategy().orElse(CapabilityProvisioningStrategy.DEPLOY_DIRECTLY)
                                != CapabilityProvisioningStrategy.USE_EXTERNAL)
                .withSecretName(providedCapability.getSpec().getExternallyProvisionedService()
                        .map(ExternallyProvidedService::getAdminSecretName).orElse(null))
                .withHost(providedCapability.getSpec().getExternallyProvisionedService()
                        .map(ExternallyProvidedService::getHost).orElse(null))
                .withPort(providedCapability.getSpec().getExternallyProvisionedService()
                        .flatMap(ExternallyProvidedService::getPort).orElse(null))
                .withTablespace(resolveParameterIfPresent(providedCapability, "tablespace"))
                .withDatabaseName(resolveParameterIfPresent(providedCapability, "databaseName"))
                .withProvidedCapabilityScope(providedCapability.getSpec().getScope().orElse(CapabilityScope.NAMESPACE))
                .withJdbcParameters(ofNullable(providedCapability.getSpec().getCapabilityParameters()).orElse(Collections.emptyMap())
                        .entrySet().stream()
                        .filter(entry -> entry.getKey().startsWith(ProvidedDatabaseCapability.JDBC_PARAMETER_PREFIX))
                        .map(entry -> Map.entry(entry.getKey().substring(ProvidedDatabaseCapability.JDBC_PARAMETER_PREFIX.length()),
                                entry.getValue()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                .endSpec()
                .build();
        if (!ResourceUtils.customResourceOwns(providedCapability, entandoDatabaseServiceToSyncTo)) {
            entandoDatabaseServiceToSyncTo.getMetadata().getOwnerReferences().add(ResourceUtils.buildOwnerReference(providedCapability));
        }
        return entandoDatabaseServiceToSyncTo;
    }

    private String resolveParameterIfPresent(ProvidedCapability providedCapability, String paramName) {
        return ofNullable(providedCapability.getSpec().getCapabilityParameters()).map(params -> params.get(paramName)).orElse(null);
    }

    private ProvidedCapability syncFromImplementingResourceToCapability(EntandoDatabaseService resourceToProcess) {
        ProvidedCapability capabilityToSyncTo = k8sClient.load(
                ProvidedCapability.class,
                resourceToProcess.getMetadata().getNamespace(),
                resourceToProcess.getMetadata().getName());
        final ProvidedCapabilityBuilder builder;
        builder = new ProvidedCapabilityBuilder(Objects.requireNonNullElseGet(capabilityToSyncTo,
                () -> new ProvidedCapability(new ObjectMeta(), new CapabilityRequirement())));
        final HashMap<String, String> parameters = new HashMap<>();
        resourceToProcess.getSpec().getDatabaseName().ifPresent(s -> parameters.put("databaseName", s));
        resourceToProcess.getSpec().getTablespace().ifPresent(s -> parameters.put("tablespace", s));
        resourceToProcess.getSpec().getJdbcParameters()
                .forEach((key, value) -> parameters.put(ProvidedDatabaseCapability.JDBC_PARAMETER_PREFIX + key, value));
        NestedCapabilityRequirementFluent<ProvidedCapabilityBuilder> specBuilder = builder
                .editMetadata()
                .withNamespace(resourceToProcess.getMetadata().getNamespace())
                .withName(resourceToProcess.getMetadata().getName())
                .withLabels(resourceToProcess.getMetadata().getLabels())
                .endMetadata()
                .editSpec()
                .withCapability(StandardCapability.DBMS)
                .withImplementation(StandardCapabilityImplementation
                        .valueOf(resourceToProcess.getSpec().getDbms().orElse(DbmsVendor.POSTGRESQL).name()))
                .withSelector(resourceToProcess.getSpec().getProvidedCapabilityScope().filter(CapabilityScope.LABELED::equals)
                        .map(s -> resourceToProcess.getMetadata().getLabels()).orElse(null))
                .withCapabilityRequirementScope(resourceToProcess.getSpec().getProvidedCapabilityScope().orElse(CapabilityScope.NAMESPACE))
                .withCapabilityParameters(parameters);
        if (resourceToProcess.getSpec().getCreateDeployment().orElse(false)) {
            specBuilder = specBuilder.withProvisioningStrategy(CapabilityProvisioningStrategy.DEPLOY_DIRECTLY);
        } else {
            specBuilder = specBuilder.withProvisioningStrategy(CapabilityProvisioningStrategy.USE_EXTERNAL)
                    .withNewExternallyProvidedService()
                    .withHost(resourceToProcess.getSpec().getHost().orElseThrow(IllegalStateException::new))
                    .withPort(resourceToProcess.getSpec().getPort().orElseThrow(IllegalStateException::new))
                    .withAdminSecretName(resourceToProcess.getSpec().getSecretName().orElse(null))
                    .endExternallyProvidedService();
        }
        capabilityToSyncTo = specBuilder.endSpec().build();
        if (!ResourceUtils.customResourceOwns(resourceToProcess, capabilityToSyncTo)) {
            //If we are here, it means one of two things:
            // 1. This is a new EntandoDatabaseService and we need to create a ProvidedCapability owned by it so that the
            // ControllerCoordinator won't process changes against the ProvidedCapability.
            // 2. the user has removed the ownerReference from the original EntandoDatabaseService, thus indicating
            //that he is taking control of it.  Now we change control over to the original EntandoDatabaseService, make it own the
            // ProvidedCapability so that only its events will be listened to.
            capabilityToSyncTo.getMetadata().getOwnerReferences().add(ResourceUtils.buildOwnerReference(resourceToProcess));
        }
        return capabilityToSyncTo;
    }

}
