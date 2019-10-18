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

import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaimStatus;
import io.fabric8.kubernetes.api.model.PodStatus;
import io.fabric8.kubernetes.api.model.ServiceStatus;
import io.fabric8.kubernetes.api.model.apps.DeploymentStatus;
import io.fabric8.kubernetes.api.model.extensions.IngressStatus;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import org.entando.kubernetes.model.AbstractServerStatus;
import org.entando.kubernetes.model.DbServerStatus;
import org.entando.kubernetes.model.DbmsImageVendor;
import org.entando.kubernetes.model.EntandoControllerFailure;
import org.entando.kubernetes.model.EntandoCustomResourceStatus;
import org.entando.kubernetes.model.SampleWriter;
import org.entando.kubernetes.model.WebServerStatus;
import org.entando.kubernetes.model.keycloakserver.KeycloakServer;
import org.entando.kubernetes.model.keycloakserver.KeycloakServerSpec;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("in-process")
public class EntandoCustomResourceStatusTest {

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    //Bug in PMD. This method is used twice
    private static void populateStatus(AbstractServerStatus dbServerStatus) {
        dbServerStatus.setPodStatus(new PodStatus());
        dbServerStatus.setDeploymentStatus(new DeploymentStatus());
        dbServerStatus.setEntandoControllerFailure(new EntandoControllerFailure());
        dbServerStatus.setPersistentVolumeClaimStatuses(Arrays.asList(new PersistentVolumeClaimStatus()));
        dbServerStatus.setServiceStatus(new ServiceStatus());
    }

    @Test
    public void testSerializeDeserialize() {
        DbServerStatus dbServerStatus = new DbServerStatus();
        dbServerStatus.setQualifier("db");
        populateStatus(dbServerStatus);
        WebServerStatus webServerStatus = new WebServerStatus();
        webServerStatus.setQualifier("web");
        webServerStatus.setIngressStatus(new IngressStatus());
        populateStatus(dbServerStatus);
        KeycloakServer keycloakServer = new KeycloakServer();
        keycloakServer.getMetadata().setGeneration(3L);
        keycloakServer.setSpec(new KeycloakServerSpec(null, DbmsImageVendor.ORACLE, null, null, null));
        keycloakServer.getMetadata().setName("test-keycloak");
        keycloakServer.setStatus(new EntandoCustomResourceStatus());
        keycloakServer.getStatus().putServerStatus(dbServerStatus);
        keycloakServer.getStatus().putServerStatus(webServerStatus);
        Path sample = SampleWriter.writeSample(Paths.get("target"), keycloakServer);
        KeycloakServer actual = SampleWriter.readSample(sample, KeycloakServer.class);
        assertNotNull(actual.getStatus().forDbQualifiedBy("db").get().getDeploymentStatus());

    }
}
