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

import java.util.Collections;
import org.entando.kubernetes.model.capability.CapabilityProvisioningStrategy;
import org.entando.kubernetes.model.capability.CapabilityScope;
import org.entando.kubernetes.model.capability.ProvidedCapability;
import org.entando.kubernetes.model.capability.ProvidedCapabilityBuilder;
import org.entando.kubernetes.model.capability.StandardCapability;
import org.entando.kubernetes.model.capability.StandardCapabilityImplementation;
import org.entando.kubernetes.model.common.DbmsVendor;
import org.entando.kubernetes.model.common.ResourceReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//Sonar doesn't pick up that this class is extended in other packages
@SuppressWarnings("java:S5786")
public abstract class AbstractProvidedCapabilityTest implements CustomResourceTestUtil {

    protected static final String MY_NAMESPACE = TestConfig.calculateNameSpace("my-namespace");
    public static final String MY_ADMIN_SECRET = "my-admin-secret";
    public static final String MY_CAPABILITY = "my-capability";

    @BeforeEach
    public void deleteEntandoAppPluginLinks() {

        prepareNamespace(getClient().customResources(ProvidedCapability.class), MY_NAMESPACE);
    }

    @Test
    void testCreateProvidedCapability() {
        //Given
        final ProvidedCapability providedCapability = new ProvidedCapabilityBuilder()
                .withNewMetadata()
                .withName(MY_CAPABILITY)
                .withNamespace(MY_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .withCapability(StandardCapability.SSO)
                .withImplementation(StandardCapabilityImplementation.KEYCLOAK)
                .withResolutionScopePreference(CapabilityScope.NAMESPACE, CapabilityScope.CLUSTER)
                .withProvisioningStrategy(CapabilityProvisioningStrategy.USE_EXTERNAL)
                .withPreferredDbms(DbmsVendor.MYSQL)
                .withPreferredIngressHostName("myhost.com")
                .withPreferredTlsSecretName("my-secret")
                .withNewExternallyProvidedService()
                .withHost("keycloak.host.com").withPort(80).withAdminSecretName(MY_ADMIN_SECRET).withRequiresDirectConnection(true)
                .endExternallyProvidedService()
                .withSpecifiedCapability(new ResourceReference(MY_NAMESPACE, "my-keycloak"))
                .addAllToCapabilityParameters(Collections.singletonMap("frontendUrl", "http://somehost.com/auth"))
                .withSelector(Collections.singletonMap("my-label", "my-label-value"))

                .endSpec()
                .build();

        getClient().customResources(ProvidedCapability.class).inNamespace(MY_NAMESPACE)
                .create(new ProvidedCapabilityBuilder().withMetadata(providedCapability.getMetadata())
                        .withSpec(providedCapability.getSpec())
                        .build());
        //When

        ProvidedCapability actual = getClient().customResources(ProvidedCapability.class).inNamespace(MY_NAMESPACE).withName(MY_CAPABILITY)
                .get();
        //Then
        assertThat(actual.getSpec().getCapability(), is(StandardCapability.SSO));
        assertThat(actual.getSpec().getPreferredDbms().get(), is(DbmsVendor.MYSQL));
        assertThat(actual.getSpec().getPreferredIngressHostName().get(), is("myhost.com"));
        assertThat(actual.getSpec().getPreferredTlsSecretName().get(), is("my-secret"));
        assertThat(actual.getSpec().getImplementation().get(), is(StandardCapabilityImplementation.KEYCLOAK));
        assertThat(actual.getSpec().getResolutionScopePreference().get(0), is(CapabilityScope.NAMESPACE));
        assertThat(actual.getSpec().getResolutionScopePreference().get(1), is(CapabilityScope.CLUSTER));
        assertThat(actual.getSpec().getProvisioningStrategy().get(), is(CapabilityProvisioningStrategy.USE_EXTERNAL));
        assertThat(actual.getSpec().getExternallyProvisionedService().get().getHost(), is("keycloak.host.com"));
        assertThat(actual.getSpec().getExternallyProvisionedService().get().getPort().get(), is(80));
        assertThat(actual.getSpec().getExternallyProvisionedService().get().getAdminSecretName(), is(MY_ADMIN_SECRET));
        assertThat(actual.getSpec().getSpecifiedCapability().get().getName(), is("my-keycloak"));
        assertThat(actual.getSpec().getSpecifiedCapability().get().getNamespace().get(), is(MY_NAMESPACE));
        assertThat(actual.getSpec().getSelector().get("my-label"), is("my-label-value"));

    }

    @Test
    void testEditProvidedCapability() {
        //Given
        final ProvidedCapability providedCapability = new ProvidedCapabilityBuilder()
                .withNewMetadata()
                .withName(MY_CAPABILITY)
                .withNamespace(MY_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .withCapability(StandardCapability.DBMS)
                .withImplementation(StandardCapabilityImplementation.MYSQL)
                .withResolutionScopePreference(CapabilityScope.LABELED)
                .withProvisioningStrategy(CapabilityProvisioningStrategy.DELEGATE_TO_OPERATOR)
                .withNewExternallyProvidedService()
                .withHost("other.host.com").withPath("/other").withPort(81).withAdminSecretName("other-secret")
                .withRequiresDirectConnection(false)

                .endExternallyProvidedService()
                .withSpecifiedCapability(new ResourceReference("some-namespace", "some-keycloak"))
                .addAllToCapabilityParameters(Collections.singletonMap("frontendUrl", "http://somehost.com/auth"))

                .endSpec()
                .build();
        //When
        //We are not using the mock server here because of a known bug

        getClient().customResources(ProvidedCapability.class).inNamespace(MY_NAMESPACE).create(providedCapability);

        ProvidedCapability actual = getClient().customResources(ProvidedCapability.class).inNamespace(MY_NAMESPACE).withName(MY_CAPABILITY)
                .patch(new ProvidedCapabilityBuilder(
                        getClient().customResources(ProvidedCapability.class).inNamespace(MY_NAMESPACE).withName(MY_CAPABILITY).fromServer()
                                .get())
                        .editMetadata().addToLabels("my-label", "my-value")
                        .endMetadata()
                        .editSpec()
                        .withCapability(StandardCapability.SSO)
                        .withImplementation(StandardCapabilityImplementation.KEYCLOAK)
                        .withResolutionScopePreference(CapabilityScope.NAMESPACE, CapabilityScope.CLUSTER)
                        .withProvisioningStrategy(CapabilityProvisioningStrategy.USE_EXTERNAL)
                        .withNewExternallyProvidedService()
                        .withHost("keycloak.host.com").withPath("/auth").withPort(80).withAdminSecretName(MY_ADMIN_SECRET)
                        .withRequiresDirectConnection(true)
                        .endExternallyProvidedService()
                        .withSpecifiedCapability(new ResourceReference(MY_NAMESPACE, "my-keycloak"))
                        .addAllToCapabilityParameters(Collections.singletonMap("frontendUrl", "http://somehost.com/auth"))
                        .endSpec()
                        .build());
        //Then
        //Then
        assertThat(actual.getSpec().getCapability(), is(StandardCapability.SSO));
        assertThat(actual.getSpec().getImplementation().get(), is(StandardCapabilityImplementation.KEYCLOAK));
        assertThat(actual.getSpec().getResolutionScopePreference().get(0), is(CapabilityScope.NAMESPACE));
        assertThat(actual.getSpec().getResolutionScopePreference().get(1), is(CapabilityScope.CLUSTER));
        assertThat(actual.getSpec().getProvisioningStrategy().get(), is(CapabilityProvisioningStrategy.USE_EXTERNAL));
        assertThat(actual.getSpec().getExternallyProvisionedService().get().getHost(), is("keycloak.host.com"));
        assertThat(actual.getSpec().getExternallyProvisionedService().get().getPath().get(), is("/auth"));
        assertThat(actual.getSpec().getExternallyProvisionedService().get().getPort().get(), is(80));
        assertThat(actual.getSpec().getExternallyProvisionedService().get().getAdminSecretName(), is(MY_ADMIN_SECRET));
        assertThat(actual.getSpec().getSpecifiedCapability().get().getName(), is("my-keycloak"));
        assertThat(actual.getSpec().getSpecifiedCapability().get().getNamespace().get(), is(MY_NAMESPACE));
    }

}
