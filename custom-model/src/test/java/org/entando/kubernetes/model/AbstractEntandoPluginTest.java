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

import static org.entando.kubernetes.model.plugin.PluginSecurityLevel.STRICT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.client.CustomResourceList;
import io.fabric8.kubernetes.client.dsl.internal.CustomResourceOperationsImpl;
import java.util.Arrays;
import java.util.Collections;
import org.entando.kubernetes.model.plugin.DoneableEntandoPlugin;
import org.entando.kubernetes.model.plugin.EntandoPlugin;
import org.entando.kubernetes.model.plugin.EntandoPluginBuilder;
import org.entando.kubernetes.model.plugin.ExpectedRole;
import org.entando.kubernetes.model.plugin.Permission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class AbstractEntandoPluginTest implements CustomResourceTestUtil {

    protected static final String MY_PLUGIN = "my-plugin";
    protected static final String MY_NAMESPACE = TestConfig.calculateNameSpace("my-namespace");
    private static final String MY_CLUSTER_INFRASTRUCTURE = "my-cluster-infrastructure";
    private static final String MYHOST_COM = "myhost.com";
    private static final String MY_TLS_SECRET = "my-tls-secret";
    private static final String IMAGE = "entando/someplugin:1.0.2";
    private static final String SOME_CONNECTION = "some-connection";
    private static final String INGRESS_PATH = "/plugsy";
    private static final String ACTUATOR_HEALTH = "/actuator/health";
    private static final String ENTANDO_APP = "entando-app";
    private static final String SUPERUSER = "superuser";
    private static final String MY_COMPANION_CONTAINER = "my-companion-container";
    private static final String ADMIN = "admin";
    private static final String ADMINISTRATOR = "Administrator";
    private static final String PARAMETER_NAME = "env";
    private static final String PARAMETER_VALUE = "B";
    private static final String MY_KEYCLOAK_SECRET = "my-keycloak-secret";
    private EntandoResourceOperationsRegistry registry;

    @BeforeEach
    public void deleteEntandoPlugins() {
        registry = new EntandoResourceOperationsRegistry(getClient());
        prepareNamespace(entandoPlugins(), MY_NAMESPACE);
    }

    @Test
    public void testCreateEntandoPlugin() {
        //Given
        EntandoPlugin entandoPlugin = new EntandoPluginBuilder()
                .withNewMetadata().withName(MY_PLUGIN)
                .withNamespace(MY_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .withDbms(DbmsVendor.MYSQL)
                .withImage(IMAGE)
                .addNewConnectionConfigName(SOME_CONNECTION)
                .withReplicas(5)
                .withIngressPath(INGRESS_PATH)
                .withHealthCheckPath(ACTUATOR_HEALTH)
                .withIngressHostName(MYHOST_COM)
                .withTlsSecretName(MY_TLS_SECRET)
                .addNewPermission(ENTANDO_APP, SUPERUSER)
                .addNewRole(ADMIN, ADMINISTRATOR)
                .addNewCompanionContainer(MY_COMPANION_CONTAINER)
                .addNewParameter(PARAMETER_NAME, PARAMETER_VALUE)
                .withSecurityLevel(STRICT)
                .withKeycloakSecretToUse(MY_KEYCLOAK_SECRET)
                .withClusterInfrastructureSecretToUse(MY_CLUSTER_INFRASTRUCTURE)
                .endSpec()
                .build();
        entandoPlugins().inNamespace(MY_NAMESPACE).createNew().withMetadata(entandoPlugin.getMetadata()).withSpec(entandoPlugin.getSpec())
                .done();
        //When
        EntandoPlugin actual = entandoPlugins().inNamespace(MY_NAMESPACE).withName(MY_PLUGIN).get();
        //Then
        assertThat(actual.getSpec().getDbms().get(), is(DbmsVendor.MYSQL));
        assertThat(actual.getSpec().getImage(), is(IMAGE));
        assertThat(actual.getSpec().getKeycloakSecretToUse().get(), is(MY_KEYCLOAK_SECRET));
        assertThat(actual.getKeycloakSecretToUse().get(), is(MY_KEYCLOAK_SECRET));
        assertThat(actual.getSpec().getTlsSecretName().get(), is(MY_TLS_SECRET));
        assertThat(actual.getTlsSecretName().get(), is(MY_TLS_SECRET));
        assertThat(actual.getSpec().getIngressHostName().get(), is(MYHOST_COM));
        assertThat(actual.getIngressHostName().get(), is(MYHOST_COM));
        assertThat(actual.getSpec().getConnectionConfigNames(), is(Arrays.asList(SOME_CONNECTION)));
        assertThat(actual.getSpec().getCompanionContainers(), is(Arrays.asList(MY_COMPANION_CONTAINER)));
        assertThat(actual.getSpec().getPermissions().get(0).getClientId(), is(ENTANDO_APP));
        assertThat(actual.getSpec().getPermissions().get(0).getRole(), is(SUPERUSER));
        assertThat(findParameter(actual.getSpec(), PARAMETER_NAME).get().getValue(), is(PARAMETER_VALUE));
        assertThat(actual.getSpec().getRoles().get(0).getCode(), is(ADMIN));
        assertThat(actual.getSpec().getRoles().get(0).getName(), is(ADMINISTRATOR));
        assertThat(actual.getSpec().getSecurityLevel().get(), is(STRICT));
        assertThat(actual.getSpec().getIngressPath(), is(INGRESS_PATH));
        assertThat(actual.getSpec().getHealthCheckPath(), is(ACTUATOR_HEALTH));
        assertThat(actual.getSpec().getReplicas().get(), is(5));
        assertThat(actual.getSpec().getClusterInfrastructureSecretToUse().get(), is(MY_CLUSTER_INFRASTRUCTURE));
        assertThat(actual.getMetadata().getName(), is(MY_PLUGIN));
        assertThat(actual.getStatus(), is(notNullValue()));
    }

    @Test
    public void testEditEntandoPlugin() {
        //Given
        EntandoPlugin entandoPlugin = new EntandoPluginBuilder()
                .withNewMetadata()
                .withName(MY_PLUGIN)
                .withNamespace(MY_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .withDbms(DbmsVendor.POSTGRESQL)
                .withImage("entando/enoatherplugin:1.0.2")
                .addNewConnectionConfigName("another-connection")
                .withReplicas(5)
                .withIngressPath(INGRESS_PATH)
                .withHealthCheckPath("/actuator/unhealth")
                .addNewPermission("entando-usermgment", "subuser")
                .addNewRole("user", "User")
                .addNewCompanionContainer(MY_COMPANION_CONTAINER)
                .addNewParameter(PARAMETER_NAME, "A")
                .withSecurityLevel(STRICT)
                .withKeycloakSecretToUse("another-keycloak-secret")
                .withClusterInfrastructureSecretToUse("another-cluster-infrastructure")
                .endSpec()
                .build();
        //When
        entandoPlugins().inNamespace(MY_NAMESPACE).create(entandoPlugin);
        EntandoPlugin actual = entandoPlugins().inNamespace(MY_NAMESPACE).withName(MY_PLUGIN).edit()
                .editMetadata().addToLabels("my-label", "my-value")
                .endMetadata()
                .editSpec()
                .withDbms(DbmsVendor.MYSQL)
                .withImage(IMAGE)
                .withConnectionConfigNames(Arrays.asList(SOME_CONNECTION))
                .withReplicas(5)
                .withHealthCheckPath(ACTUATOR_HEALTH)
                .withIngressHostName(MYHOST_COM)
                .withTlsSecretName(MY_TLS_SECRET)
                .withPermissions(Arrays.asList(new Permission(ENTANDO_APP, SUPERUSER)))
                .withRoles(Arrays.asList(new ExpectedRole(ADMIN, ADMINISTRATOR)))
                .withParameters(Collections.singletonList(new EnvVar(PARAMETER_NAME, PARAMETER_VALUE, null)))
                .withSecurityLevel(STRICT)
                .withKeycloakSecretToUse(MY_KEYCLOAK_SECRET)
                .withClusterInfrastructureSecretToUse(MY_CLUSTER_INFRASTRUCTURE)
                .endSpec()
                .withStatus(new WebServerStatus("some-qualifier"))
                .withStatus(new DbServerStatus("another-qualifier"))
                .withPhase(EntandoDeploymentPhase.STARTED)
                .done();
        //Then
        assertThat(actual.getSpec().getDbms().get(), is(DbmsVendor.MYSQL));
        assertThat(actual.getSpec().getImage(), is(IMAGE));
        assertThat(actual.getSpec().getKeycloakSecretToUse().get(), is(MY_KEYCLOAK_SECRET));
        assertThat(actual.getKeycloakSecretToUse().get(), is(MY_KEYCLOAK_SECRET));
        assertThat(actual.getSpec().getTlsSecretName().get(), is(MY_TLS_SECRET));
        assertThat(actual.getTlsSecretName().get(), is(MY_TLS_SECRET));
        assertThat(actual.getSpec().getIngressHostName().get(), is(MYHOST_COM));
        assertThat(actual.getIngressHostName().get(), is(MYHOST_COM));
        assertThat(actual.getSpec().getConnectionConfigNames(), is(Arrays.asList(SOME_CONNECTION)));
        assertThat(actual.getSpec().getConnectionConfigNames(), is(Arrays.asList(SOME_CONNECTION)));
        assertThat(actual.getSpec().getPermissions().get(0).getClientId(), is(ENTANDO_APP));
        assertThat(actual.getSpec().getPermissions().get(0).getRole(), is(SUPERUSER));
        assertThat(actual.getSpec().getRoles().get(0).getCode(), is(ADMIN));
        assertThat(actual.getSpec().getRoles().get(0).getName(), is(ADMINISTRATOR));
        assertThat(actual.getSpec().getSecurityLevel().get(), is(STRICT));
        assertThat(findParameter(actual.getSpec(), PARAMETER_NAME).get().getValue(), is(PARAMETER_VALUE));
        assertThat(actual.getSpec().getReplicas().get(), is(5));
        assertThat(actual.getSpec().getClusterInfrastructureSecretToUse().get(), is(MY_CLUSTER_INFRASTRUCTURE));
        assertThat(actual.getMetadata().getName(), is(MY_PLUGIN));
    }

    protected CustomResourceOperationsImpl<EntandoPlugin, CustomResourceList<EntandoPlugin>, DoneableEntandoPlugin> entandoPlugins() {
        return registry.getOperations(EntandoPlugin.class);
    }

}
