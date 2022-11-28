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

import org.entando.kubernetes.model.capability.CapabilityProvisioningStrategy;
import org.entando.kubernetes.model.capability.CapabilityScope;
import org.entando.kubernetes.model.common.DbmsVendor;
import org.entando.kubernetes.model.common.EntandoDeploymentPhase;
import org.entando.kubernetes.model.common.ServerStatus;
import org.entando.kubernetes.model.keycloakserver.EntandoKeycloakServer;
import org.entando.kubernetes.model.keycloakserver.EntandoKeycloakServerBuilder;
import org.entando.kubernetes.model.keycloakserver.StandardKeycloakImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//Sonar doesn't pick up that this class is extended in other packages
@SuppressWarnings("java:S5786")
public abstract class AbstractEntandoKeycloakServerTest implements CustomResourceTestUtil {

    protected static final String MY_KEYCLOAK = "my-keycloak";
    protected static final String MY_NAMESPACE = TestConfig.calculateNameSpace("my-namespace");
    private static final String ENTANDO_SOMEKEYCLOAK = "entando/somekeycloak";
    private static final String MYHOST_COM = "myhost.com";
    private static final String MY_TLS_SECRET = "my-tls-secret";
    public static final String HTTP_MY_FRONTEND_URL = "http://my.frontend/url";
    public static final String MY_ADMIN_SECRET = "my-admin-secret";
    public static final String MY_REALM = "my-realm";

    @BeforeEach
    public void deleteEntandoKeycloakServer() {
        prepareNamespace(getClient().customResources(EntandoKeycloakServer.class), MY_NAMESPACE);
    }

    @Test
    void testCreateEntandoKeycloakServer() {
        //Given
        EntandoKeycloakServer keycloakServer = new EntandoKeycloakServerBuilder()
                .withNewMetadata().withName(MY_KEYCLOAK)
                .withNamespace(MY_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .withDbms(DbmsVendor.MYSQL)
                .withCustomImage(ENTANDO_SOMEKEYCLOAK)
                .withReplicas(5)
                .withStandardImage(StandardKeycloakImage.REDHAT_SSO)
                .withFrontEndUrl(HTTP_MY_FRONTEND_URL)
                .withAdminSecretName(MY_ADMIN_SECRET)
                .withProvisioningStrategy(CapabilityProvisioningStrategy.USE_EXTERNAL)
                .withDefault(true)
                .withIngressHostName(MYHOST_COM)
                .withTlsSecretName(MY_TLS_SECRET)
                .endSpec()
                .build();
        getClient().customResources(EntandoKeycloakServer.class).inNamespace(MY_NAMESPACE).create(keycloakServer);
        //When
        EntandoKeycloakServer actual = getClient().customResources(EntandoKeycloakServer.class).inNamespace(MY_NAMESPACE)
                .withName(MY_KEYCLOAK).get();

        //Then
        assertThat(actual.getSpec().getDbms().get(), is(DbmsVendor.MYSQL));
        assertThat(actual.getSpec().getFrontEndUrl().get(), is(HTTP_MY_FRONTEND_URL));
        assertThat(actual.getSpec().getAdminSecretName().get(), is(MY_ADMIN_SECRET));
        assertThat(actual.getSpec().getProvisioningStrategy().get(), is(CapabilityProvisioningStrategy.USE_EXTERNAL));
        assertThat(actual.getSpec().getStandardImage().get(), is(StandardKeycloakImage.REDHAT_SSO));
        assertThat(actual.getSpec().getCustomImage().get(), is(ENTANDO_SOMEKEYCLOAK));
        assertThat(actual.getSpec().getIngressHostName().get(), is(MYHOST_COM));
        assertThat(actual.getSpec().getReplicas().get(), is(5));
        assertThat(actual.getSpec().getTlsSecretName().get(), is(MY_TLS_SECRET));
        assertThat(actual.getSpec().isDefault(), is(true));
        assertThat(actual.getMetadata().getName(), is(MY_KEYCLOAK));
    }

    @Test
    void testEditEntandoKeycloakServer() {
        //Given
        EntandoKeycloakServer keycloakServer = new EntandoKeycloakServerBuilder()
                .withNewMetadata()
                .withName(MY_KEYCLOAK)
                .withNamespace(MY_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .withDbms(DbmsVendor.POSTGRESQL)
                .withAdminSecretName("some-other-secret")
                .withProvisioningStrategy(CapabilityProvisioningStrategy.DELEGATE_TO_OPERATOR)
                .withIngressHostName("some.other.host.com")
                .withFrontEndUrl("http://other.frontend/url")
                .withStandardImage(StandardKeycloakImage.KEYCLOAK)
                .withCustomImage("entando/anotherkeycloak")
                .withProvidedCapabilityScope(CapabilityScope.LABELED)
                .withReplicas(3)
                .withDefaultRealm("some-realm")
                .withTlsSecretName("some-othersecret")
                .withDefault(false)
                .endSpec()
                .build();

        //When
        final EntandoKeycloakServerBuilder toEdit = new EntandoKeycloakServerBuilder(
                getClient().customResources(EntandoKeycloakServer.class).inNamespace(MY_NAMESPACE).create(keycloakServer));
        EntandoKeycloakServer actual = getClient().customResources(EntandoKeycloakServer.class).inNamespace(MY_NAMESPACE)
                .withName(MY_KEYCLOAK)
                .patch(toEdit
                        .editMetadata().addToLabels("my-label", "my-value")
                        .endMetadata()
                        .editSpec()
                        .withDbms(DbmsVendor.MYSQL)
                        .withCustomImage(ENTANDO_SOMEKEYCLOAK)
                        .withStandardImage(StandardKeycloakImage.REDHAT_SSO)
                        .withFrontEndUrl(HTTP_MY_FRONTEND_URL)
                        .withDefaultRealm(MY_REALM)
                        .withAdminSecretName(MY_ADMIN_SECRET)
                        .withProvisioningStrategy(CapabilityProvisioningStrategy.USE_EXTERNAL)
                        .withIngressHostName(MYHOST_COM)
                        .withReplicas(5)
                        .withProvidedCapabilityScope(CapabilityScope.CLUSTER)
                        .withDefault(true)
                        .withTlsSecretName(MY_TLS_SECRET)
                        .endSpec()
                        .build());
        actual.getStatus().putServerStatus(new ServerStatus("some-qualifier"));
        actual.getStatus().putServerStatus(new ServerStatus("some-other-qualifier"));
        actual.getStatus().putServerStatus(new ServerStatus("some-qualifier"));
        actual.getStatus().putServerStatus(new ServerStatus("another-qualifier"));
        actual.getStatus().updateDeploymentPhase(EntandoDeploymentPhase.STARTED, 5L);
        actual = getClient().customResources(EntandoKeycloakServer.class).inNamespace(actual.getMetadata().getNamespace())
                .updateStatus(actual);
        //Then
        assertThat(actual.getSpec().getDbms().get(), is(DbmsVendor.MYSQL));
        assertThat(actual.getSpec().getFrontEndUrl().get(), is(HTTP_MY_FRONTEND_URL));
        assertThat(actual.getSpec().getStandardImage().get(), is(StandardKeycloakImage.REDHAT_SSO));
        assertThat(actual.getSpec().getAdminSecretName().get(), is(MY_ADMIN_SECRET));
        assertThat(actual.getSpec().getProvisioningStrategy().get(), is(CapabilityProvisioningStrategy.USE_EXTERNAL));
        assertThat(actual.getSpec().getCustomImage().get(), is(ENTANDO_SOMEKEYCLOAK));
        assertThat(actual.getSpec().getFrontEndUrl().get(), is(HTTP_MY_FRONTEND_URL));
        assertThat(actual.getSpec().getStandardImage().get(), is(StandardKeycloakImage.REDHAT_SSO));
        assertThat(actual.getSpec().getIngressHostName().get(), is(MYHOST_COM));
        assertThat(actual.getSpec().getReplicas().get(), is(5));
        assertThat(actual.getSpec().getTlsSecretName().get(), is(MY_TLS_SECRET));
        assertThat(actual.getSpec().getProvidedCapabilityScope().get(), is(CapabilityScope.CLUSTER));
        assertThat(actual.getSpec().getDefaultRealm().get(), is(MY_REALM));
        assertThat(actual.getSpec().isDefault(), is(true));
        assertThat(actual.getMetadata().getName(), is(MY_KEYCLOAK));
        assertThat("the status reflects", actual.getStatus().getServerStatus("some-qualifier").isPresent());
        assertThat("the status reflects", actual.getStatus().getServerStatus("some-other-qualifier").isPresent());
        assertThat("the status reflects", actual.getStatus().getServerStatus("another-qualifier").isPresent());
        assertThat(actual.getStatus().getPhase(), is(EntandoDeploymentPhase.STARTED));
        assertThat(actual.getStatus().getObservedGeneration(), is(5L));
    }

}
