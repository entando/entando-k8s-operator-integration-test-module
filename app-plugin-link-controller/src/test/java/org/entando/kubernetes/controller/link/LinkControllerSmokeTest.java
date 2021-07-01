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

package org.entando.kubernetes.controller.link;

import static io.qameta.allure.Allure.attachment;
import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watcher.Action;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.entando.kubernetes.controller.support.client.impl.DefaultSimpleK8SClient;
import org.entando.kubernetes.controller.support.client.impl.EntandoOperatorTestConfig;
import org.entando.kubernetes.controller.support.client.impl.SupportProducer;
import org.entando.kubernetes.controller.support.client.impl.integrationtesthelpers.FluentIntegrationTesting;
import org.entando.kubernetes.controller.support.client.impl.integrationtesthelpers.HttpTestHelper;
import org.entando.kubernetes.controller.support.client.impl.integrationtesthelpers.TestFixturePreparation;
import org.entando.kubernetes.controller.support.common.EntandoOperatorConfig;
import org.entando.kubernetes.model.app.EntandoApp;
import org.entando.kubernetes.model.app.EntandoAppBuilder;
import org.entando.kubernetes.model.capability.ProvidedCapability;
import org.entando.kubernetes.model.common.DbmsVendor;
import org.entando.kubernetes.model.common.EntandoCustomResource;
import org.entando.kubernetes.model.link.EntandoAppPluginLink;
import org.entando.kubernetes.model.link.EntandoAppPluginLinkBuilder;
import org.entando.kubernetes.model.plugin.EntandoPlugin;
import org.entando.kubernetes.model.plugin.EntandoPluginBuilder;
import org.entando.kubernetes.test.common.KeycloakTestCapabilityProvider;
import org.entando.kubernetes.test.e2etest.ControllerExecutor;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

@Tags({@Tag("smoke"), @Tag("allure"), @Tag("post-deployment"), @Tag("inter-process")})
@Feature("As an Entando Operator users, I want to use a Docker container to process an EntandoAppPluginLink so that I don't need to "
        + "know any of its implementation details to use it.")
class LinkControllerSmokeTest implements FluentIntegrationTesting {

    private static final String MY_NAMESPACE = EntandoOperatorTestConfig.calculateNameSpace("my-namespace");
    public static final String MY_PLUGIN = EntandoOperatorTestConfig.calculateName("my-plugin");
    private static final String MY_APP = EntandoOperatorTestConfig.calculateName("my-app");
    private final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
    private EntandoPlugin entandoPlugin;
    private EntandoApp entandoApp;
    final String pluginHostname = MY_PLUGIN + "-" + MY_NAMESPACE + "." + EntandoOperatorConfig.getDefaultRoutingSuffix()
            .orElse("apps.serv.run");
    final String appHostname = MY_APP + "-" + MY_NAMESPACE + "." + EntandoOperatorConfig.getDefaultRoutingSuffix()
            .orElse("apps.serv.run");
    private final ExecutorService scheduler = Executors.newFixedThreadPool(4);
    private EntandoAppPluginLink link;
    final KubernetesClient client = new SupportProducer().getKubernetesClient();
    final DefaultSimpleK8SClient simpleClient = new DefaultSimpleK8SClient(client);

    @Test
    @Description("Should successfully connect to newly deployed Plugin using the Ingress of the EntandoApp")
    void testLink() throws InterruptedException {
        step("Given I have a clean namespace", () -> {
            TestFixturePreparation.prepareTestFixture(client, deleteAll(EntandoPlugin.class).fromNamespace(MY_NAMESPACE)
                    .deleteAll(EntandoApp.class).fromNamespace(MY_NAMESPACE)
                    .deleteAll(EntandoAppPluginLink.class).fromNamespace(MY_NAMESPACE)
            );
        });
        step("And I have configured a ProvidedCapability for SSO in the namespace", () -> {
            final ProvidedCapability providedCapability = new KeycloakTestCapabilityProvider(simpleClient, MY_NAMESPACE)
                    .provideKeycloakCapability();
            attachment("SSO Capability", objectMapper.writeValueAsString(providedCapability));
        });
        step("And I have created an EntandoApp custom resource", () -> {
            this.entandoApp = simpleClient.entandoResources()
                    .createOrPatchEntandoResource(
                            new EntandoAppBuilder()
                                    .withNewMetadata()
                                    .withNamespace(MY_NAMESPACE)
                                    .withName(MY_APP)
                                    .endMetadata()
                                    .withNewSpec()
                                    .withIngressHostName(appHostname)
                                    .withDbms(DbmsVendor.EMBEDDED)
                                    .endSpec()
                                    .build()
                    );
            //force initialization of CRD/Name map
            simpleClient.entandoResources()
                    .loadCustomResource(entandoApp.getApiVersion(), entandoApp.getKind(), entandoApp.getMetadata().getNamespace(),
                            entandoApp.getMetadata().getName());
            startControllerFor(simpleClient, "entando-k8s-app-controller", this.entandoApp, "0.0.0-SNAPSHOT-PR-32-26");
            attachment("Entando App", objectMapper.writeValueAsString(this.entandoApp));
        });
        step("And I have created an EntandoPlugin custom resource", () -> {
            this.entandoPlugin = simpleClient.entandoResources()
                    .createOrPatchEntandoResource(
                            new EntandoPluginBuilder()
                                    .withNewMetadata()
                                    .withNamespace(MY_NAMESPACE)
                                    .withName(MY_PLUGIN)
                                    .endMetadata()
                                    .withNewSpec()
                                    .withIngressHostName(pluginHostname)
                                    .withImage("entando/entando-avatar-plugin:6.0.5")
                                    .withHealthCheckPath("/management/health")
                                    .withIngressPath("/avatarPlugin")
                                    .withDbms(DbmsVendor.EMBEDDED)
                                    .endSpec()
                                    .build()
                    );
            startControllerFor(simpleClient, "entando-k8s-plugin-controller", this.entandoPlugin, "0.0.0-SNAPSHOT-PR-27-11");
            attachment("Entando Plugin", objectMapper.writeValueAsString(this.entandoPlugin));
        });
        step("When I create an EntandoAppPluginLink to link the two custom resources", () -> {
            this.link = simpleClient.entandoResources()
                    .createOrPatchEntandoResource(
                            new EntandoAppPluginLinkBuilder()
                                    .withNewMetadata()
                                    .withNamespace(MY_NAMESPACE)
                                    .withName("my-link")
                                    .endMetadata()
                                    .withNewSpec()
                                    .withEntandoApp(MY_NAMESPACE, MY_APP)
                                    .withEntandoPlugin(MY_NAMESPACE, MY_PLUGIN)
                                    .endSpec()
                                    .build()
                    );
            startControllerFor(simpleClient, "entando-k8s-app-plugin-link-controller", this.link,
                    EntandoOperatorTestConfig.getVersionOfImageUnderTest().orElse("0.0.0-SNAPSHOT-PR1-3"));
            attachment("Entando App-Plugin Link", objectMapper.writeValueAsString(this.link));
        });
        System.out.println("waiting....");
        scheduler.shutdown();
        scheduler.awaitTermination(5, TimeUnit.MINUTES);
        System.out.println("completed");
        step("Then I can successfully access the Plugin's health URL from the EntandoApp's hostname", () -> {
            final String strUrl =
                    HttpTestHelper.getDefaultProtocol() + "://" + appHostname
                            + "/avatarPlugin/management/health";
            await().atMost(1, TimeUnit.MINUTES).ignoreExceptions().until(() -> HttpTestHelper.statusOk(strUrl));
            assertThat(HttpTestHelper.statusOk(strUrl)).isTrue();
        });
        step("And I can successfully access the Plugin's health URL from the EntandoPlugin's hostname", () -> {
            final String strUrl =
                    HttpTestHelper.getDefaultProtocol() + "://" + pluginHostname
                            + "/avatarPlugin/management/health";
            await().atMost(1, TimeUnit.MINUTES).ignoreExceptions().until(() -> HttpTestHelper.statusOk(strUrl));
            assertThat(HttpTestHelper.statusOk(strUrl)).isTrue();
        });
    }

    private void startControllerFor(DefaultSimpleK8SClient simpleClient, String s, EntandoCustomResource customResource,
            String versionToUse) {
        ControllerExecutor executor = new ControllerExecutor(MY_NAMESPACE, simpleClient, r -> s);
        scheduler.submit(() -> {
            try {
                executor.runControllerFor(Action.ADDED, customResource, versionToUse);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
