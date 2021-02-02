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
import org.entando.kubernetes.model.keycloakserver.DoneableEntandoKeycloakServer;
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
    private EntandoResourceOperationsRegistry registry;

    @BeforeEach
    public void deleteEntandoKeycloakServer() {
        registry = new EntandoResourceOperationsRegistry(getClient());
        prepareNamespace(keycloakServers(), MY_NAMESPACE);
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
                .withDefault(true)
                .withIngressHostName(MYHOST_COM)
                .withTlsSecretName(MY_TLS_SECRET)
                .endSpec()
                .build();
        keycloakServers().inNamespace(MY_NAMESPACE).createNew().withMetadata(keycloakServer.getMetadata())
                .withSpec(keycloakServer.getSpec()).done();
        //When
        EntandoKeycloakServer actual = keycloakServers().inNamespace(MY_NAMESPACE).withName(MY_KEYCLOAK).get();

        //Then
        assertThat(actual.getSpec().getDbms().get(), is(DbmsVendor.MYSQL));
        assertThat(actual.getSpec().getFrontEndUrl().get(), is(HTTP_MY_FRONTEND_URL));
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
                .withIngressHostName("some.other.host.com")
                .withFrontEndUrl("http://other.frontend/url")
                .withStandardImage(StandardKeycloakImage.KEYCLOAK)
                .withCustomImage("entando/anotherkeycloak")
                .withReplicas(3)
                .withTlsSecretName("some-othersecret")
                .withDefault(false)
                .endSpec()
                .build();

        //When
        //We are not using the mock server here because of a known bug
        EntandoKeycloakServer actual = editEntandoKeycloakServer(keycloakServer)
                .editMetadata().addToLabels("my-label", "my-value")
                .endMetadata()
                .editSpec()
                .withDbms(DbmsVendor.MYSQL)
                .withCustomImage(ENTANDO_SOMEKEYCLOAK)
                .withStandardImage(StandardKeycloakImage.REDHAT_SSO)
                .withFrontEndUrl(HTTP_MY_FRONTEND_URL)
                .withIngressHostName(MYHOST_COM)
                .withReplicas(5)
                .withDefault(true)
                .withTlsSecretName(MY_TLS_SECRET)
                .endSpec()
                .done();
        actual.getStatus().putServerStatus(new WebServerStatus("some-qualifier"));
        actual.getStatus().putServerStatus(new WebServerStatus("some-other-qualifier"));
        actual.getStatus().putServerStatus(new WebServerStatus("some-qualifier"));
        actual.getStatus().putServerStatus(new DbServerStatus("another-qualifier"));
        actual.getStatus().updateDeploymentPhase(EntandoDeploymentPhase.STARTED, 5L);
        actual = keycloakServers().inNamespace(actual.getMetadata().getNamespace()).updateStatus(actual);
        //Then
        assertThat(actual.getSpec().getDbms().get(), is(DbmsVendor.MYSQL));
        assertThat(actual.getSpec().getFrontEndUrl().get(), is(HTTP_MY_FRONTEND_URL));
        assertThat(actual.getSpec().getStandardImage().get(), is(StandardKeycloakImage.REDHAT_SSO));
        assertThat(actual.getSpec().getCustomImage().get(), is(ENTANDO_SOMEKEYCLOAK));
        assertThat(actual.getSpec().getFrontEndUrl().get(), is(HTTP_MY_FRONTEND_URL));
        assertThat(actual.getSpec().getStandardImage().get(), is(StandardKeycloakImage.REDHAT_SSO));
        assertThat(actual.getSpec().getIngressHostName().get(), is(MYHOST_COM));
        assertThat(actual.getSpec().getReplicas().get(), is(5));
        assertThat(actual.getSpec().getTlsSecretName().get(), is(MY_TLS_SECRET));
        assertThat(actual.getSpec().isDefault(), is(true));
        assertThat(actual.getMetadata().getName(), is(MY_KEYCLOAK));
        assertThat("the status reflects", actual.getStatus().forServerQualifiedBy("some-qualifier").isPresent());
        assertThat("the status reflects", actual.getStatus().forServerQualifiedBy("some-other-qualifier").isPresent());
        assertThat("the status reflects", actual.getStatus().forDbQualifiedBy("another-qualifier").isPresent());
        assertThat(actual.getStatus().getEntandoDeploymentPhase(), is(EntandoDeploymentPhase.STARTED));
        assertThat(actual.getStatus().getObservedGeneration(), is(5L));
    }

    protected final DoneableEntandoKeycloakServer editEntandoKeycloakServer(EntandoKeycloakServer keycloakServer) {
        keycloakServers().inNamespace(MY_NAMESPACE).create(keycloakServer);
        return keycloakServers().inNamespace(MY_NAMESPACE).withName(MY_KEYCLOAK).edit();
    }

    protected CustomResourceOperationsImpl<
            EntandoKeycloakServer,
            CustomResourceList<EntandoKeycloakServer>,
            DoneableEntandoKeycloakServer> keycloakServers() {
        return registry.getOperations(EntandoKeycloakServer.class);
    }
}
