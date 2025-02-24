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

package org.entando.kubernetes.app.plugin.link.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.entando.kubernetes.controller.link.EntandoAppPluginLinkController;
import org.entando.kubernetes.controller.link.support.InProcessDeploymentLinker;
import org.entando.kubernetes.controller.spi.capability.CapabilityProvider;
import org.entando.kubernetes.controller.spi.client.KubernetesClientForControllers;
import org.entando.kubernetes.controller.spi.command.DeploymentProcessor;
import org.entando.kubernetes.controller.spi.common.EntandoOperatorSpiConfigProperty;
import org.entando.kubernetes.controller.support.client.SimpleKeycloakClient;
import org.entando.kubernetes.controller.support.client.doubles.SimpleK8SClientDouble;
import org.entando.kubernetes.model.app.EntandoApp;
import org.entando.kubernetes.model.capability.ProvidedCapability;
import org.entando.kubernetes.model.link.EntandoAppPluginLink;
import org.entando.kubernetes.model.plugin.EntandoPlugin;
import org.entando.kubernetes.test.common.ControllerTestHelper;
import org.entando.kubernetes.test.common.LogInterceptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

public abstract class LinkControllerTestBase implements ControllerTestHelper {

    protected final SimpleK8SClientDouble client = new SimpleK8SClientDouble();
    protected final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
    @Mock
    protected SimpleKeycloakClient keycloakClient;
    protected EntandoApp entandoApp;
    protected EntandoPlugin entandoPlugin;
    protected ProvidedCapability sso;
    protected EntandoAppPluginLink link;

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
    void registerCrds() throws IOException {
        System.setProperty(EntandoOperatorSpiConfigProperty.ENTANDO_CONTROLLER_POD_NAME.getJvmSystemProperty(), "some-pod");
        registerCrd("crd/providedcapabilities.entando.org.crd.yaml");
        registerCrd("crd/entandoapps.entando.org.crd.yaml");
        registerCrd("crd/entandoplugins.entando.org.crd.yaml");
        registerCrd("crd/entandoapppluginlinks.entando.org.crd.yaml");
        LogInterceptor.listenToClass(EntandoAppPluginLinkController.class);
    }

    @AfterEach
    void resetSystemProps() {
        System.clearProperty(EntandoOperatorSpiConfigProperty.ENTANDO_CONTROLLER_POD_NAME.getJvmSystemProperty());
    }

    @Override
    public Runnable createController(KubernetesClientForControllers kubernetesClientForControllers, DeploymentProcessor deploymentProcessor,
            CapabilityProvider capabilityProvider) {
        return new EntandoAppPluginLinkController(kubernetesClientForControllers,
                new InProcessDeploymentLinker(getClient(), keycloakClient));
    }
}
