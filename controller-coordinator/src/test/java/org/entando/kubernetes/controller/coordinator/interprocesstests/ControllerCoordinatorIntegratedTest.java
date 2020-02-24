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

package org.entando.kubernetes.controller.coordinator.interprocesstests;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.NamespacedKubernetesClient;
import io.fabric8.kubernetes.client.Watch;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.Watcher.Action;
import io.fabric8.kubernetes.client.dsl.FilterWatchListDeletable;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.quarkus.runtime.StartupEvent;
import java.util.concurrent.TimeUnit;
import org.entando.kubernetes.client.DefaultIngressClient;
import org.entando.kubernetes.controller.EntandoOperatorConfigProperty;
import org.entando.kubernetes.controller.KubeUtils;
import org.entando.kubernetes.controller.coordinator.AbstractControllerCoordinatorTest;
import org.entando.kubernetes.controller.coordinator.EntandoControllerCoordinator;
import org.entando.kubernetes.controller.coordinator.ImageVersionPreparation;
import org.entando.kubernetes.controller.creators.IngressCreator;
import org.entando.kubernetes.controller.integrationtest.support.EntandoOperatorTestConfig;
import org.entando.kubernetes.controller.integrationtest.support.EntandoOperatorTestConfig.TestTarget;
import org.entando.kubernetes.controller.integrationtest.support.TestFixturePreparation;
import org.entando.kubernetes.model.DbmsVendor;
import org.entando.kubernetes.model.EntandoBaseCustomResource;
import org.entando.kubernetes.model.EntandoDeploymentPhase;
import org.entando.kubernetes.model.compositeapp.DoneableEntandoCompositeApp;
import org.entando.kubernetes.model.compositeapp.EntandoCompositeApp;
import org.entando.kubernetes.model.compositeapp.EntandoCompositeAppBuilder;
import org.entando.kubernetes.model.compositeapp.EntandoCompositeAppOperationFactory;
import org.entando.kubernetes.model.keycloakserver.DoneableEntandoKeycloakServer;
import org.entando.kubernetes.model.keycloakserver.EntandoKeycloakServer;
import org.entando.kubernetes.model.keycloakserver.EntandoKeycloakServerOperationFactory;
import org.entando.kubernetes.model.plugin.DoneableEntandoPlugin;
import org.entando.kubernetes.model.plugin.EntandoPlugin;
import org.entando.kubernetes.model.plugin.EntandoPluginOperationFactory;
import org.entando.kubernetes.model.plugin.PluginSecurityLevel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("inter-process")
public class ControllerCoordinatorIntegratedTest extends AbstractControllerCoordinatorTest {

    private static NamespacedKubernetesClient client;
    private String domainSuffix;

    private static NamespacedKubernetesClient staticGetClient() {
        if (client == null) {
            client = TestFixturePreparation.newClient().inNamespace(NAMESPACE);
        }
        return client;
    }

    @BeforeAll
    public static void prepareCoordinator() {
        NamespacedKubernetesClient client = staticGetClient();
        clearNamespace(client);
        if (EntandoOperatorTestConfig.getTestTarget() == TestTarget.STANDALONE) {
            new EntandoControllerCoordinator(client).onStartup(new StartupEvent());
        } else {
            //Should be installed by helm chart in pipeline
        }
    }

    /**
     * Adding this test as a kind of e2e test to ensure state gets propagate correctly all the way through th container hierarchy.
     */
    @Test
    public void testExecuteCompositeAppControllerPod() throws JsonProcessingException {
        //Given I have a clean namespace
        KubernetesClient client = getClient();
        clearNamespace(client);
        //and the Coordinator observes this namespace
        System.setProperty(EntandoOperatorConfigProperty.ENTANDO_K8S_OPERATOR_NAMESPACE_TO_OBSERVE.getJvmSystemProperty(),
                client.getNamespace());
        //And I have a config map with the Entando KeycloakController's image information
        final String keycloakControllerVersionToExpect = ensureKeycloakControllerVersion();
        final String pluginControllerVersionToExpect = ensurePluginControllerVersion();
        final String compositeAppControllerVersionToExpect = ensureCompositeAppControllerVersion();
        //When I create a new EntandoCompositeApp with an EntandoKeycloakServer and EntandoPlugin component

        EntandoCompositeApp appToCreate = new EntandoCompositeAppBuilder()
                .withNewMetadata().withName(MY_APP).withNamespace(client.getNamespace()).endMetadata()
                .withNewSpec()
                .addNewEntandoKeycloakServer()
                .withNewMetadata().withName(KEYCLOAK_NAME).withNamespace(client.getNamespace()).endMetadata()
                .withNewSpec()
                .withDefault(true)
                .withDbms(DbmsVendor.NONE)
                .withIngressHostName(KEYCLOAK_NAME + "." + getDomainSuffix())
                .endSpec()
                .endEntandoKeycloakServer()
                .addNewEntandoPlugin()
                .withNewMetadata().withName(PLUGIN_NAME).endMetadata().withNewSpec()
                .withImage("entando/entando-avatar-plugin")
                .withDbms(DbmsVendor.POSTGRESQL)
                .withReplicas(1)
                .withIngressHostName(PLUGIN_NAME + "." + getDomainSuffix())
                .withHealthCheckPath("/management/health")
                .withIngressPath("/avatarPlugin")
                .withSecurityLevel(PluginSecurityLevel.STRICT)
                .endSpec()
                .endEntandoPlugin()
                .endSpec()
                .build();
        EntandoCompositeApp app = EntandoCompositeAppOperationFactory.produceAllEntandoCompositeApps(getClient())
                .inNamespace(NAMESPACE)
                .create(appToCreate);
        //Then I expect to see the keycloak controller pod
        FilterWatchListDeletable<Pod, PodList, Boolean, Watch, Watcher<Pod>> keycloakControllerList = client.pods()
                .inNamespace(client.getNamespace())
                .withLabel(KubeUtils.ENTANDO_RESOURCE_KIND_LABEL_NAME, "EntandoKeycloakServer")
                .withLabel("EntandoKeycloakServer", app.getSpec().getComponents().get(0).getMetadata().getName());
        await().ignoreExceptions().atMost(30, TimeUnit.SECONDS).until(() -> keycloakControllerList.list().getItems().size() > 0);
        Pod theKeycloakControllerPod = keycloakControllerList.list().getItems().get(0);
        //and the EntandoKeycloakServer resource has been saved to K8S under the EntandoCompositeApp
        Resource<EntandoKeycloakServer, DoneableEntandoKeycloakServer> keycloakGettable = EntandoKeycloakServerOperationFactory
                .produceAllEntandoKeycloakServers(getClient()).inNamespace(NAMESPACE).withName(KEYCLOAK_NAME);
        await().ignoreExceptions().atMost(15, TimeUnit.SECONDS).until(
                () -> keycloakGettable.get().getMetadata().getOwnerReferences().get(0).getUid().equals(app.getMetadata().getUid())
        );
        //and the EntandoKeycloakServer resource's identifying information has been passed to the controller Pod
        assertThat(theVariableNamed("ENTANDO_RESOURCE_ACTION").on(thePrimaryContainerOn(theKeycloakControllerPod)),
                is(Action.ADDED.name()));
        assertThat(theVariableNamed("ENTANDO_RESOURCE_NAME").on(thePrimaryContainerOn(theKeycloakControllerPod)),
                is(app.getSpec().getComponents().get(0).getMetadata().getName()));
        assertThat(theVariableNamed("ENTANDO_RESOURCE_NAMESPACE").on(thePrimaryContainerOn(theKeycloakControllerPod)),
                is(app.getMetadata().getNamespace()));
        //With the correct version of the controller image specified
        assertTrue(thePrimaryContainerOn(theKeycloakControllerPod).getImage().endsWith(keycloakControllerVersionToExpect));
        //And its status reflecting on the EntandoCompositeApp
        Resource<EntandoCompositeApp, DoneableEntandoCompositeApp> appGettable =
                EntandoCompositeAppOperationFactory
                        .produceAllEntandoCompositeApps(client)
                        .inNamespace(client.getNamespace()).withName(MY_APP);
        await().ignoreExceptions().atMost(180, TimeUnit.SECONDS).until(
                () -> appGettable.fromServer().get().getStatus().forServerQualifiedBy(KEYCLOAK_NAME).get().getPodStatus() != null
        );
        //And the plugin controller pod
        FilterWatchListDeletable<Pod, PodList, Boolean, Watch, Watcher<Pod>> pluginControllerList = client.pods()
                .inNamespace(client.getNamespace())
                .withLabel(KubeUtils.ENTANDO_RESOURCE_KIND_LABEL_NAME, "EntandoPlugin")
                .withLabel("EntandoPlugin", app.getSpec().getComponents().get(1).getMetadata().getName());
        await().ignoreExceptions().atMost(30, TimeUnit.SECONDS).until(() -> pluginControllerList.list().getItems().size() > 0);
        Pod thePluginControllerPod = pluginControllerList.list().getItems().get(0);
        //and the EntandoKeycloakServer resource has been saved to K8S under the EntandoCompositeApp
        Resource<EntandoPlugin, DoneableEntandoPlugin> pluginGettable = EntandoPluginOperationFactory
                .produceAllEntandoPlugins(getClient()).inNamespace(NAMESPACE).withName(PLUGIN_NAME);
        await().ignoreExceptions().atMost(15, TimeUnit.SECONDS).until(
                () -> pluginGettable.get().getMetadata().getOwnerReferences().get(0).getUid(), is(app.getMetadata().getUid())
        );
        //and the EntandoKeycloakServer resource's identifying information has been passed to the controller Pod
        assertThat(theVariableNamed("ENTANDO_RESOURCE_ACTION").on(thePrimaryContainerOn(thePluginControllerPod)), is(Action.ADDED.name()));
        assertThat(theVariableNamed("ENTANDO_RESOURCE_NAME").on(thePrimaryContainerOn(thePluginControllerPod)),
                is(app.getSpec().getComponents().get(1).getMetadata().getName()));
        assertThat(theVariableNamed("ENTANDO_RESOURCE_NAMESPACE").on(thePrimaryContainerOn(thePluginControllerPod)),
                is(app.getMetadata().getNamespace()));
        //With the correct version specified
        assertTrue(thePrimaryContainerOn(thePluginControllerPod).getImage().endsWith(pluginControllerVersionToExpect));
        //And its status reflecting on the EntandoCompositeApp
        await().ignoreExceptions().atMost(180, TimeUnit.SECONDS).until(
                () -> appGettable.fromServer().get().getStatus().forServerQualifiedBy(PLUGIN_NAME).get().getPodStatus() != null
        );
        //And the EntandoCompositeApp is in a finished state
        await().ignoreExceptions().atMost(30, TimeUnit.SECONDS).until(() -> hasFinished(appGettable));
    }

    private String ensureCompositeAppControllerVersion() throws JsonProcessingException {
        ImageVersionPreparation imageVersionPreparation = new ImageVersionPreparation(getClient());
        return imageVersionPreparation.ensureImageVersion("entando-k8s-composite-app-controller", "6.0.12");
    }

    @Override
    protected KubernetesClient getClient() {
        return staticGetClient();
    }

    @Override
    protected <T extends EntandoBaseCustomResource> void afterCreate(T resource) {
    }

    private String getDomainSuffix() {
        if (domainSuffix == null) {
            domainSuffix = IngressCreator.determineRoutingSuffix(DefaultIngressClient.resolveMasterHostname(this.getClient()));
        }
        return domainSuffix;
    }

    protected String ensurePluginControllerVersion() throws JsonProcessingException {
        ImageVersionPreparation imageVersionPreparation = new ImageVersionPreparation(getClient());
        return imageVersionPreparation.ensureImageVersion("entando-k8s-plugin-controller", "6.0.2");
    }

    private boolean hasFinished(Resource<EntandoCompositeApp, DoneableEntandoCompositeApp> appGettable) {
        EntandoDeploymentPhase phase = appGettable.fromServer().get().getStatus().getEntandoDeploymentPhase();
        return phase == EntandoDeploymentPhase.SUCCESSFUL || phase == EntandoDeploymentPhase.FAILED;
    }

}
