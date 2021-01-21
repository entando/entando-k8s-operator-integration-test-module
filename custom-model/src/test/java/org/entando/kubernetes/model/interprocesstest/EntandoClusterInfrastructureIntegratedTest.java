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

package org.entando.kubernetes.model.interprocesstest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import io.fabric8.kubernetes.client.AutoAdaptableKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.entando.kubernetes.model.AbstractEntandoClusterInfrastructureTest;
import org.entando.kubernetes.model.EntandoDeploymentPhase;
import org.entando.kubernetes.model.infrastructure.EntandoClusterInfrastructure;
import org.entando.kubernetes.model.infrastructure.EntandoClusterInfrastructureBuilder;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

@Tags({@Tag("inter-process"), @Tag("pre-deployment")})
public class EntandoClusterInfrastructureIntegratedTest extends AbstractEntandoClusterInfrastructureTest {

    private final KubernetesClient client = new AutoAdaptableKubernetesClient();

    @Override
    public KubernetesClient getClient() {
        return client;
    }

    @Test
    public void testUnderstandingOfGeneration() {
        EntandoClusterInfrastructure keycloakServer = new EntandoClusterInfrastructureBuilder()
                .withNewMetadata()
                .withName(MY_ENTANDO_CLUSTER_INFRASTRUCTURE)
                .withNamespace(MY_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .withReplicas(3)
                .withNewKeycloakToUse()
                .withNamespace("somenamespace")
                .withName("another-keycloak")
                .withRealm("somerealm")
                .withPublicClientId("some-public-client")
                .endKeycloakToUse()
                .withTlsSecretName("some-othersecret")
                .withDefault(false)
                .endSpec()
                .build();
        EntandoClusterInfrastructure eci1 = entandoInfrastructure().inNamespace(MY_NAMESPACE)
                .create(keycloakServer);
        assertThat(eci1.getMetadata().getGeneration(), is(1L));
        EntandoClusterInfrastructure eci2 = entandoInfrastructure().inNamespace(MY_NAMESPACE).withName(MY_ENTANDO_CLUSTER_INFRASTRUCTURE)
                .edit()
                .editSpec()
                .withServiceAccountToUse("asdfasdfasdfsad")
                .endSpec()
                .done();
        assertThat(eci2.getMetadata().getGeneration(), is(2L));
        EntandoClusterInfrastructure eci3 = entandoInfrastructure().inNamespace(MY_NAMESPACE).withName(MY_ENTANDO_CLUSTER_INFRASTRUCTURE)
                .edit()
                .editMetadata().addToLabels("my-label", "my-value")
                .endMetadata()
                .done();
        assertThat(eci3.getMetadata().getGeneration(), is(2L));
        eci3.getStatus().updateDeploymentPhase(EntandoDeploymentPhase.FAILED, eci2.getMetadata().getGeneration());
        EntandoClusterInfrastructure eci4 = entandoInfrastructure().inNamespace(MY_NAMESPACE).withName(MY_ENTANDO_CLUSTER_INFRASTRUCTURE)
                .updateStatus(eci3);
        assertThat(eci4.getMetadata().getGeneration(), is(2L));
        assertThat(eci4.getStatus().getObservedGeneration(), is(2L));
        EntandoClusterInfrastructure eci5 = entandoInfrastructure().inNamespace(MY_NAMESPACE).withName(MY_ENTANDO_CLUSTER_INFRASTRUCTURE)
                .edit()
                .editSpec()
                .withServiceAccountToUse("asdf-asdf")
                .endSpec()
                .done();
        assertThat(eci5.getMetadata().getGeneration(), is(3L));

    }
}
