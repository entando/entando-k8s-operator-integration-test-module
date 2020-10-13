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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import io.fabric8.kubernetes.client.CustomResourceList;
import io.fabric8.kubernetes.client.dsl.internal.CustomResourceOperationsImpl;
import org.entando.kubernetes.model.infrastructure.DoneableEntandoClusterInfrastructure;
import org.entando.kubernetes.model.infrastructure.EntandoClusterInfrastructure;
import org.entando.kubernetes.model.infrastructure.EntandoClusterInfrastructureBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class AbstractEntandoClusterInfrastructureTest implements CustomResourceTestUtil {

    protected static final String MY_ENTANDO_CLUSTER_INFRASTRUCTURE = "my-entando-cluster-infrastructure";
    protected static final String MY_NAMESPACE = TestConfig.calculateNameSpace("my-namespace");
    private static final String MYHOST_COM = "myhost.com";
    private static final String MY_TLS_SECRET = "my-tls-secret";
    private static final String MY_KEYCLOAK_SECRET = "my-keycloak-secret";
    private EntandoResourceOperationsRegistry registry;

    @BeforeEach
    public void deleteEntandoClusterInfrastructure() {
        registry = new EntandoResourceOperationsRegistry(getClient());
        prepareNamespace(entandoInfrastructure(), MY_NAMESPACE);
    }

    @Test
    public void testCreateEntandoClusterInfrastructure() {
        //Given
        EntandoClusterInfrastructure clusterInfrastructure = new EntandoClusterInfrastructureBuilder()
                .withNewMetadata().withName(MY_ENTANDO_CLUSTER_INFRASTRUCTURE)
                .withNamespace(MY_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .withDbms(DbmsVendor.MYSQL)
                .withReplicas(5)
                .withIngressHostName(MYHOST_COM)
                .withTlsSecretName(MY_TLS_SECRET)
                .withKeycloakSecretToUse(MY_KEYCLOAK_SECRET)
                .withDefault(true)
                .endSpec()
                .build();
        entandoInfrastructure().inNamespace(MY_NAMESPACE).createNew().withMetadata(clusterInfrastructure.getMetadata())
                .withSpec(clusterInfrastructure.getSpec()).done();
        //When
        EntandoClusterInfrastructure actual = entandoInfrastructure().inNamespace(MY_NAMESPACE).withName(MY_ENTANDO_CLUSTER_INFRASTRUCTURE)
                .get();
        //Then
        assertThat(actual.getSpec().getDbms().get(), is(DbmsVendor.MYSQL));
        assertThat(actual.getSpec().getKeycloakSecretToUse().get(), is(MY_KEYCLOAK_SECRET));
        assertThat(actual.getKeycloakSecretToUse().get(), is(MY_KEYCLOAK_SECRET));
        assertThat(actual.getSpec().getIngressHostName().get(), is(MYHOST_COM));
        assertThat(actual.getSpec().getReplicas().get(), is(5));
        assertThat(actual.getSpec().getTlsSecretName().get(), is(MY_TLS_SECRET));
        assertThat(actual.getSpec().isDefault(), is(true));
        assertThat(actual.getTlsSecretName().get(), is(MY_TLS_SECRET));
        assertThat(actual.getMetadata().getName(), is(MY_ENTANDO_CLUSTER_INFRASTRUCTURE));
    }

    @Test
    public void testEditEntandoClusterInfrastructure() {
        //Given
        EntandoClusterInfrastructure keycloakServer = new EntandoClusterInfrastructureBuilder()
                .withNewMetadata()
                .withName(MY_ENTANDO_CLUSTER_INFRASTRUCTURE)
                .withNamespace(MY_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .withDbms(DbmsVendor.POSTGRESQL)
                .withIngressHostName(MYHOST_COM)
                .withReplicas(3)
                .withKeycloakSecretToUse("some-othersecret")
                .withTlsSecretName("some-othersecret")
                .withDefault(false)
                .endSpec()
                .build();
        //When
        //We are not using the mock server here because of a known bug
        entandoInfrastructure().inNamespace(MY_NAMESPACE).create(keycloakServer);
        EntandoClusterInfrastructure actual = entandoInfrastructure().inNamespace(MY_NAMESPACE).withName(MY_ENTANDO_CLUSTER_INFRASTRUCTURE)
                .edit()
                .editMetadata().addToLabels("my-label", "my-value")
                .endMetadata()
                .editSpec()
                .withDbms(DbmsVendor.MYSQL)
                .withIngressHostName(MYHOST_COM)
                .withReplicas(5)
                .withKeycloakSecretToUse(MY_KEYCLOAK_SECRET)
                .withTlsSecretName(MY_TLS_SECRET)
                .withDefault(true)
                .endSpec()
                .withStatus(new WebServerStatus("some-qualifier"))
                .withStatus(new WebServerStatus("some-other-qualifier"))
                .withStatus(new WebServerStatus("some-qualifier"))
                .withStatus(new DbServerStatus("another-qualifier"))
                .withPhase(EntandoDeploymentPhase.STARTED)
                .done();
        //Then
        assertThat(actual.getSpec().getDbms().get(), is(DbmsVendor.MYSQL));
        assertThat(actual.getSpec().getKeycloakSecretToUse().get(), is(MY_KEYCLOAK_SECRET));
        assertThat(actual.getSpec().getIngressHostName().get(), is(MYHOST_COM));
        assertThat(actual.getSpec().getReplicas().get(), is(5));
        assertThat(actual.getSpec().getTlsSecretName().get(), is(MY_TLS_SECRET));
        assertThat(actual.getSpec().isDefault(), is(true));
        assertThat(actual.getMetadata().getName(), is(MY_ENTANDO_CLUSTER_INFRASTRUCTURE));
        assertThat("the status reflects", actual.getStatus().forServerQualifiedBy("some-qualifier").isPresent());
        assertThat("the status reflects", actual.getStatus().forServerQualifiedBy("some-other-qualifier").isPresent());
        assertThat("the status reflects", actual.getStatus().forDbQualifiedBy("another-qualifier").isPresent());
    }

    protected CustomResourceOperationsImpl<EntandoClusterInfrastructure, CustomResourceList<EntandoClusterInfrastructure>,
            DoneableEntandoClusterInfrastructure> entandoInfrastructure() {
        return registry.getOperations(EntandoClusterInfrastructure.class);
    }
}
