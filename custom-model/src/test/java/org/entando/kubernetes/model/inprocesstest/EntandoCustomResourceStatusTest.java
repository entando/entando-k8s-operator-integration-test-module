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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.entando.kubernetes.model.SampleWriter;
import org.entando.kubernetes.model.common.DbmsVendor;
import org.entando.kubernetes.model.common.EntandoControllerFailureBuilder;
import org.entando.kubernetes.model.common.EntandoCustomResourceStatus;
import org.entando.kubernetes.model.common.EntandoDeploymentPhase;
import org.entando.kubernetes.model.common.ServerStatus;
import org.entando.kubernetes.model.keycloakserver.EntandoKeycloakServer;
import org.entando.kubernetes.model.keycloakserver.EntandoKeycloakServerSpecBuilder;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

@Tags({@Tag("in-process"), @Tag("pre-deployment")})
class EntandoCustomResourceStatusTest {

    private static void populateStatus(ServerStatus serverStatus) {
        serverStatus.setDeploymentName("my-deployment");
        serverStatus.setServiceName("my-service");
        serverStatus.setAdminSecretName("my-admin-secret");
        serverStatus.putDerivedDeploymentParameter("database-name", "my-database");
        serverStatus.putPodPhase("initPod1", "Completed");
        serverStatus.putPodPhase("pod1", "Running");
        serverStatus.putPersistentVolumeClaimPhase("pvc1", "Bound");
        serverStatus.withOriginatingCustomResource(
                        new EntandoKeycloakServer(
                                new ObjectMetaBuilder().withNamespace("my-namespace").withName("my-capability").build(), null))
                .withOriginatingControllerPod("controller-namespace", "my-pod")
                .addToWebContexts("server", "/my-path")
                .withSsoRealm("my-realm")
                .withSsoClientId("my-client")
                .finish();
    }

    @Test
    void testSerializeDeserialize() {
        ServerStatus internalServerStatus = new ServerStatus("db");
        populateStatus(internalServerStatus);
        ServerStatus zebServerStatus = new ServerStatus("zeb");
        zebServerStatus.setIngressName("zy-ingress");
        populateStatus(zebServerStatus);
        long start = System.currentTimeMillis();
        await().until(() -> System.currentTimeMillis() - start > 1000);
        ServerStatus exposedServerStatus = new ServerStatus("web");
        exposedServerStatus.setIngressName("my-ingress");
        exposedServerStatus.setExternalBaseUrl("http://myhost.com/path");
        exposedServerStatus.finishWith(
                new EntandoControllerFailureBuilder().withFailedObjectKind("Ingress").withFailedObjectName("MyIngress")
                        .withMessage("Ingress Failed").build());
        populateStatus(exposedServerStatus);
        EntandoKeycloakServer keycloakServer = new EntandoKeycloakServer();
        keycloakServer.getMetadata().setGeneration(3L);
        keycloakServer
                .setSpec(
                        new EntandoKeycloakServerSpecBuilder().withDbms(DbmsVendor.ORACLE).build());
        keycloakServer.getMetadata().setName("test-keycloak");
        keycloakServer.setStatus(new EntandoCustomResourceStatus());
        keycloakServer.getStatus().putServerStatus(internalServerStatus);
        keycloakServer.getStatus().putServerStatus(exposedServerStatus);
        keycloakServer.getStatus().putServerStatus(zebServerStatus);
        Path sample = SampleWriter.writeSample(Paths.get("target"), keycloakServer);
        EntandoKeycloakServer actual = SampleWriter.readSample(sample, EntandoKeycloakServer.class);
        assertThat(actual.getStatus().getServerStatus("db").get().getDeploymentName().get(), is("my-deployment"));
        ServerStatus actualFinalStatus = new ServerStatus("web", actual.getStatus().getServerStatus("web").get());
        assertThat(actualFinalStatus.getQualifier(), is("web"));
        assertTrue(actualFinalStatus.getFinished().isPresent());
        assertThat(actualFinalStatus.getServiceName().get(), is("my-service"));
        assertThat(actualFinalStatus.getAdminSecretName().get(), is("my-admin-secret"));
        assertThat(actualFinalStatus.getIngressName().get(), is("my-ingress"));
        assertThat(actualFinalStatus.getExternalBaseUrl().get(), is("http://myhost.com/path"));
        assertThat(actualFinalStatus.getPodPhases().get("initPod1"), is("Completed"));
        assertThat(actualFinalStatus.getPodPhases().get("pod1"), is("Running"));
        assertThat(actualFinalStatus.getWebContexts().get("server"), is("/my-path"));
        assertThat(actualFinalStatus.getSsoRealm().get(), is("my-realm"));
        assertThat(actualFinalStatus.getSsoClientId().get(), is("my-client"));
        assertThat(actualFinalStatus.getPersistentVolumeClaimPhases().get("pvc1"), is("Bound"));
        assertThat(actualFinalStatus.getDerivedDeploymentParameters().get("database-name"), is("my-database"));
        assertThat(actualFinalStatus.getEntandoControllerFailure().get().getFailedObjectKind(), is("Ingress"));
        assertThat(actualFinalStatus.getEntandoControllerFailure().get().getFailedObjectName(), is("MyIngress"));
        assertThat(actualFinalStatus.getEntandoControllerFailure().get().getMessage(), is("Ingress Failed"));
        assertThat(actualFinalStatus.getOriginatingControllerPod().getNamespace().get(),
                is("controller-namespace"));
        assertThat(actualFinalStatus.getOriginatingControllerPod().getName(),
                is("my-pod"));
        assertThat(actualFinalStatus.getOriginatingCustomResource().getNamespace().get(),
                is("my-namespace"));
        assertThat(actualFinalStatus.getOriginatingCustomResource().getName(),
                is("my-capability"));

        assertThat(actual.getStatus().calculateFinalPhase(), is(EntandoDeploymentPhase.FAILED));
        assertThat(actual.getStatus().getServerStatuses().size(), is(3));

    }
}
