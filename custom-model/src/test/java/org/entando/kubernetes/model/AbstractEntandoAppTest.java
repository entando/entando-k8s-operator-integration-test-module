/*
 *
 *  * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
 *  *
 *  * This library is free software; you can redistribute it and/or modify it under
 *  * the terms of the GNU Lesser General Public License as published by the Free
 *  * Software Foundation; either version 2.1 of the License, or (at your option)
 *  * any later version.
 *  *
 *  * This library is distributed in the hope that it will be useful, but WITHOUT
 *  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 *  * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 *  * details.
 *
 */

package org.entando.kubernetes.model;

import static org.entando.kubernetes.model.app.EntandoAppOperationFactory.produceAllEntandoApps;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import io.fabric8.kubernetes.client.dsl.internal.CustomResourceOperationsImpl;
import org.entando.kubernetes.model.app.DoneableEntandoApp;
import org.entando.kubernetes.model.app.EntandoApp;
import org.entando.kubernetes.model.app.EntandoAppBuilder;
import org.entando.kubernetes.model.app.EntandoAppList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class AbstractEntandoAppTest implements CustomResourceTestUtil {

    public static final String MY_CUSTOM_SERVER_IMAGE = "somenamespace/someimage:3.2.2";
    public static final String MY_CLUSTER_INFRASTRUCTURE = "my-cluster-infrastructure";
    protected static final String MY_NAMESPACE = "my-namespace";
    protected static final String MY_APP = "my-app";
    private static final String ENTANDO_IMAGE_VERSION = "6.1.0-SNAPSHOT";
    private static final String MYINGRESS_COM = "myingress.com";
    private static final String MY_KEYCLOAK_SECRET = "my-keycloak-secret";
    private static final String MY_VALUE = "my-value";
    private static final String MY_LABEL = "my-label";
    private static final String MY_TLS_SECRET = "my-tls-secret";

    @BeforeEach
    public void deleteEntandoApps() throws InterruptedException {
        prepareNamespace(entandoApps(), MY_NAMESPACE);
    }

    @Test
    public void testCreateEntandoApp() throws InterruptedException {
        //Given
        EntandoApp entandoApp = new EntandoAppBuilder()
                .withNewMetadata().withName(MY_APP)
                .withNamespace(MY_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .withDbms(DbmsImageVendor.MYSQL)
                .withCustomServerImage(MY_CUSTOM_SERVER_IMAGE)
                .withEntandoImageVersion(ENTANDO_IMAGE_VERSION)
                .withStandardServerImage(JeeServer.WILDFLY)
                .withReplicas(5)
                .withTlsSecretName(MY_TLS_SECRET)
                .withIngressHostName(MYINGRESS_COM)
                .withKeycloakSecretToUse(MY_KEYCLOAK_SECRET)
                .withClusterInfrastructureToUse(MY_CLUSTER_INFRASTRUCTURE)
                .endSpec()
                .build();
        getClient().namespaces().createOrReplaceWithNew().withNewMetadata().withName(MY_NAMESPACE).endMetadata().done();
        entandoApps().inNamespace(MY_NAMESPACE).create(entandoApp);
        //When
        EntandoAppList list = entandoApps().inNamespace(MY_NAMESPACE).list();
        EntandoApp actual = list.getItems().get(0);
        //Then
        assertThat(actual.getSpec().getDbms().get(), is(DbmsImageVendor.MYSQL));
        assertThat(actual.getSpec().getEntandoImageVersion().get(), is(ENTANDO_IMAGE_VERSION));
        assertThat(actual.getSpec().getIngressHostName().get(), is(MYINGRESS_COM));
        assertThat(actual.getSpec().getKeycloakSecretToUse().get(), is(MY_KEYCLOAK_SECRET));
        assertThat(actual.getTlsSecretName().get(), is(MY_TLS_SECRET));
        assertThat(actual.getIngressHostName().get(), is(MYINGRESS_COM));
        assertThat(actual.getKeycloakSecretToUse().get(), is(MY_KEYCLOAK_SECRET));
        assertThat(actual.getSpec().getStandardServerImage().get(), is(JeeServer.WILDFLY));
        assertThat(actual.getSpec().getReplicas().get(), is(5));
        assertThat(actual.getSpec().getTlsSecretName().get(), is(MY_TLS_SECRET));
        assertThat(actual.getSpec().getCustomServerImage().isPresent(), is(false));//because it was overridden by a standard image
        assertThat(actual.getSpec().getClusterInfrastructureTouse().get(), is(MY_CLUSTER_INFRASTRUCTURE));
        assertThat(actual.getMetadata().getName(), is(MY_APP));
    }

    @Test
    public void testEditEntandoApp() throws InterruptedException {
        //Given
        EntandoApp entandoApp = new EntandoAppBuilder()
                .withNewMetadata()
                .withName(MY_APP)
                .withNamespace(MY_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .withDbms(DbmsImageVendor.POSTGRESQL)
                .withEntandoImageVersion("6.2.0-SNAPSHOT")
                .withCustomServerImage("asdfasdf/asdf:2")
                .withStandardServerImage(JeeServer.WILDFLY)
                .withReplicas(4)
                .withTlsSecretName("another-tls-secret")
                .withIngressHostName("anotheringress.com")
                .withKeycloakSecretToUse("another-keycloak-secret")
                .withClusterInfrastructureToUse("some-cluster-infrastructure")
                .endSpec()
                .build();
        getClient().namespaces().createOrReplaceWithNew().withNewMetadata().withName(MY_NAMESPACE).endMetadata().done();
        //When
        //We are not using the mock server here because of a known bug
        EntandoApp actual = editEntandoApp(entandoApp)
                .editMetadata().addToLabels(MY_LABEL, MY_VALUE)
                .endMetadata()
                .editSpec()
                .withDbms(DbmsImageVendor.MYSQL)
                .withEntandoImageVersion(ENTANDO_IMAGE_VERSION)
                .withStandardServerImage(JeeServer.WILDFLY)
                .withCustomServerImage(MY_CUSTOM_SERVER_IMAGE)
                .withReplicas(5)
                .withTlsSecretName(MY_TLS_SECRET)
                .withIngressHostName(MYINGRESS_COM)
                .withKeycloakSecretToUse(MY_KEYCLOAK_SECRET)
                .withClusterInfrastructureToUse(MY_CLUSTER_INFRASTRUCTURE)
                .endSpec()
                .withStatus(new WebServerStatus("some-qualifier"))
                .withStatus(new DbServerStatus("another-qualifier"))
                .withPhase(EntandoDeploymentPhase.STARTED)
                .done();
        //Then
        assertThat(actual.getSpec().getDbms().get(), is(DbmsImageVendor.MYSQL));
        assertThat(actual.getSpec().getEntandoImageVersion().get(), is(ENTANDO_IMAGE_VERSION));
        assertThat(actual.getSpec().getIngressHostName().get(), is(MYINGRESS_COM));
        assertThat(actual.getSpec().getKeycloakSecretToUse().get(), is(MY_KEYCLOAK_SECRET));
        assertThat(actual.getSpec().getStandardServerImage().isPresent(), is(false));//overridden by customServerImage
        assertThat(actual.getSpec().getCustomServerImage().get(), is(MY_CUSTOM_SERVER_IMAGE));
        assertThat(actual.getSpec().getClusterInfrastructureTouse().get(), is(MY_CLUSTER_INFRASTRUCTURE));
        assertThat(actual.getSpec().getReplicas().get(), is(5));
        assertThat(actual.getSpec().getTlsSecretName().get(), is(MY_TLS_SECRET));
        assertThat(actual.getMetadata().getLabels().get(MY_LABEL), is(MY_VALUE));
    }

    protected abstract DoneableEntandoApp editEntandoApp(EntandoApp entandoApp) throws InterruptedException;

    protected CustomResourceOperationsImpl<EntandoApp, EntandoAppList, DoneableEntandoApp> entandoApps() throws InterruptedException {
        return produceAllEntandoApps(getClient());
    }

}
