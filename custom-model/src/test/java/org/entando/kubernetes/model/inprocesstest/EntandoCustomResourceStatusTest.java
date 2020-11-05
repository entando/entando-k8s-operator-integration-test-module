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

package org.entando.kubernetes.model.inprocesstest;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaimStatus;
import io.fabric8.kubernetes.api.model.PodStatus;
import io.fabric8.kubernetes.api.model.ServiceStatus;
import io.fabric8.kubernetes.api.model.StatusBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentStatus;
import io.fabric8.kubernetes.api.model.extensions.IngressStatus;
import io.fabric8.kubernetes.client.KubernetesClientException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import org.entando.kubernetes.model.AbstractServerStatus;
import org.entando.kubernetes.model.DbServerStatus;
import org.entando.kubernetes.model.DbmsVendor;
import org.entando.kubernetes.model.EntandoControllerFailureBuilder;
import org.entando.kubernetes.model.EntandoCustomResourceStatus;
import org.entando.kubernetes.model.EntandoDeploymentPhase;
import org.entando.kubernetes.model.EntandoResourceRequirements;
import org.entando.kubernetes.model.SampleWriter;
import org.entando.kubernetes.model.WebServerStatus;
import org.entando.kubernetes.model.keycloakserver.EntandoKeycloakServer;
import org.entando.kubernetes.model.keycloakserver.EntandoKeycloakServerSpec;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

@Tags({@Tag("in-process"), @Tag("pre-deployment")})
public class EntandoCustomResourceStatusTest {

    private static void populateStatus(AbstractServerStatus dbServerStatus) {
        dbServerStatus.setInitPodStatus(new PodStatus());
        dbServerStatus.setPodStatus(new PodStatus());
        dbServerStatus.setDeploymentStatus(new DeploymentStatus());
        dbServerStatus.setPersistentVolumeClaimStatuses(Arrays.asList(new PersistentVolumeClaimStatus()));
        dbServerStatus.setServiceStatus(new ServiceStatus());
        dbServerStatus.finish();
    }

    @Test
    public void testSerializeDeserialize() {
        DbServerStatus dbServerStatus = new DbServerStatus();
        dbServerStatus.setQualifier("db");
        populateStatus(dbServerStatus);
        WebServerStatus zebServerStatus = new WebServerStatus();
        zebServerStatus.setQualifier("zeb");
        zebServerStatus.setIngressStatus(new IngressStatus());
        populateStatus(zebServerStatus);
        long start = System.currentTimeMillis();
        await().until(() -> System.currentTimeMillis() - start > 1000);
        WebServerStatus webServerStatus = new WebServerStatus();
        webServerStatus.setQualifier("web");
        webServerStatus.setIngressStatus(new IngressStatus());
        webServerStatus.finishWith(
                new EntandoControllerFailureBuilder().withFailedObjectType("Wrong").withFailedObjectName("Wrong")
                        .withException(new KubernetesClientException("Wrong", 403,
                                new StatusBuilder().withMessage("Ingress failed").withNewDetails()
                                        .withKind("Ingress")
                                        .withName("MyIngress")
                                        .endDetails().build())).build()
        );
        populateStatus(webServerStatus);
        EntandoKeycloakServer keycloakServer = new EntandoKeycloakServer();
        keycloakServer.getMetadata().setGeneration(3L);
        keycloakServer
                .setSpec(new EntandoKeycloakServerSpec(null, DbmsVendor.ORACLE, null, null, 1, true, "my-service-account",
                        Collections.emptyList(), new EntandoResourceRequirements()));
        keycloakServer.getMetadata().setName("test-keycloak");
        keycloakServer.setStatus(new EntandoCustomResourceStatus());
        keycloakServer.getStatus().putServerStatus(dbServerStatus);
        keycloakServer.getStatus().putServerStatus(webServerStatus);
        keycloakServer.getStatus().putServerStatus(zebServerStatus);
        Path sample = SampleWriter.writeSample(Paths.get("target"), keycloakServer);
        EntandoKeycloakServer actual = SampleWriter.readSample(sample, EntandoKeycloakServer.class);
        assertNotNull(actual.getStatus().forDbQualifiedBy("db").get().getDeploymentStatus());
        WebServerStatus actualFinalStatus = (WebServerStatus) actual.getStatus().findCurrentServerStatus().get();
        assertThat(actualFinalStatus.getQualifier(), is("web"));
        assertThat(actualFinalStatus.getFinished(), is(notNullValue()));
        assertThat(actualFinalStatus.getServiceStatus(), is(notNullValue()));
        assertThat(actualFinalStatus.getIngressStatus(), is(notNullValue()));
        assertThat(actualFinalStatus.getPodStatus(), is(notNullValue()));
        assertThat(actualFinalStatus.getInitPodStatus(), is(notNullValue()));
        assertThat(actualFinalStatus.getEntandoControllerFailure().getFailedObjectType(), is("Ingress"));
        assertThat(actualFinalStatus.getEntandoControllerFailure().getFailedObjectName(), is("MyIngress"));
        assertThat(actualFinalStatus.getEntandoControllerFailure().getMessage(), is("Ingress failed"));
        assertThat(actualFinalStatus.getEntandoControllerFailure().getDetailMessage(),
                containsString("io.fabric8.kubernetes.client.KubernetesClientException"));
        assertThat(actual.getStatus().calculateFinalPhase(), is(EntandoDeploymentPhase.FAILED));

    }
}
