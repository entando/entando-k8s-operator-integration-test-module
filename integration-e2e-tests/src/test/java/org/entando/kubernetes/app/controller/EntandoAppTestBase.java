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

package org.entando.kubernetes.app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.fabric8.kubernetes.api.model.LimitRange;
import io.fabric8.kubernetes.api.model.LimitRangeList;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.entando.kubernetes.controller.app.ComponentManagerDeployableContainer;
import org.entando.kubernetes.controller.app.EntandoAppController;
import org.entando.kubernetes.controller.spi.capability.CapabilityProvider;
import org.entando.kubernetes.controller.spi.client.KubernetesClientForControllers;
import org.entando.kubernetes.controller.spi.command.DeploymentProcessor;
import org.entando.kubernetes.controller.spi.common.EntandoOperatorSpiConfigProperty;
import org.entando.kubernetes.controller.support.client.SimpleKeycloakClient;
import org.entando.kubernetes.controller.support.client.doubles.SimpleK8SClientDouble;
import org.entando.kubernetes.controller.support.common.EntandoOperatorConfig;
import org.entando.kubernetes.controller.support.common.EntandoOperatorConfigProperty;
import org.entando.kubernetes.test.common.ControllerTestHelper;
import org.entando.kubernetes.test.common.FluentTraversals;
import org.entando.kubernetes.test.common.LogInterceptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Answers;
import org.mockito.Mock;

abstract class EntandoAppTestBase implements FluentTraversals, ControllerTestHelper {

    protected static final String ROUTING_SUFFIX = EntandoOperatorConfig.getDefaultRoutingSuffix().orElse("entando.org");
    protected final SimpleK8SClientDouble client = new SimpleK8SClientDouble();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
    protected final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
    @Mock
    private SimpleKeycloakClient keycloakClient;
    @Mock
    private KubernetesClient kubernetesClient;
    @Mock
    private MixedOperation<LimitRange, LimitRangeList, Resource<LimitRange>> limitRangesMixOp;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private MixedOperation<Secret, SecretList, Resource<Secret>> secretOps;

    @Override
    public Optional<SimpleKeycloakClient> getKeycloakClient() {
        return Optional.of(keycloakClient);
    }

    @Override
    public SimpleK8SClientDouble getClient() {
        return client;
    }

    @Override
    public ScheduledExecutorService getScheduler() {
        return scheduledExecutorService;
    }

    @BeforeEach
    void setup() throws IOException {
        registerCrds();

        when(kubernetesClient.getNamespace()).thenReturn("" + MY_NAMESPACE + "");
        when(kubernetesClient.limitRanges()).thenReturn(limitRangesMixOp);
        when(limitRangesMixOp.inNamespace(anyString())).thenReturn(limitRangesMixOp);
        when(limitRangesMixOp.createOrReplace(any(LimitRange.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
    }

    private void registerCrds() throws IOException {
        registerCrd("crd/providedcapabilities.entando.org.crd.yaml");
        registerCrd("crd/entandoapps.entando.org.crd.yaml");
        registerCrd("testresources.test.org.crd.yaml");
        LogInterceptor.listenToClass(EntandoAppController.class);
    }

    void initSecretsMock() {
        when(kubernetesClient.secrets()).thenReturn(secretOps);
        when(secretOps.inNamespace(anyString()).withName(anyString()).get()).thenReturn(null);
    }

    @AfterEach
    void resetSystemProps() {
        System.clearProperty(EntandoOperatorSpiConfigProperty.ENTANDO_POD_READINESS_TIMEOUT_SECONDS.getJvmSystemProperty());
        System.clearProperty(EntandoOperatorSpiConfigProperty.ENTANDO_POD_COMPLETION_TIMEOUT_SECONDS.getJvmSystemProperty());
        System.clearProperty(EntandoOperatorSpiConfigProperty.ENTANDO_K8S_OPERATOR_COMPLIANCE_MODE.getJvmSystemProperty());
        System.clearProperty(EntandoOperatorSpiConfigProperty.ENTANDO_CA_SECRET_NAME.getJvmSystemProperty());
        System.clearProperty(EntandoOperatorConfigProperty.ENTANDO_TLS_SECRET_NAME.getJvmSystemProperty());
        System.clearProperty(EntandoOperatorConfigProperty.ENTANDO_DOCKER_IMAGE_VERSION_FALLBACK.getJvmSystemProperty());
        System.clearProperty(EntandoOperatorConfigProperty.ENTANDO_REQUIRES_FILESYSTEM_GROUP_OVERRIDE.getJvmSystemProperty());
        System.clearProperty(EntandoOperatorConfigProperty.ENTANDO_INGRESS_CLASS.getJvmSystemProperty());
        System.clearProperty(EntandoOperatorSpiConfigProperty.ENTANDO_K8S_OPERATOR_DEFAULT_CLUSTERED_STORAGE_CLASS.getJvmSystemProperty());
        System.clearProperty(
                EntandoOperatorSpiConfigProperty.ENTANDO_K8S_OPERATOR_DEFAULT_NON_CLUSTERED_STORAGE_CLASS.getJvmSystemProperty());
        System.clearProperty(ComponentManagerDeployableContainer.ENTANDO_ECR_POSTINIT);
        LogInterceptor.reset();
    }

    @Override
    public Runnable createController(KubernetesClientForControllers kubernetesClientForControllers, DeploymentProcessor deploymentProcessor,
            CapabilityProvider capabilityProvider) {
        return new EntandoAppController(kubernetesClientForControllers, deploymentProcessor, capabilityProvider, kubernetesClient);
    }

    public KubernetesClient getKubernetesClient() {
        return kubernetesClient;
    }
}
