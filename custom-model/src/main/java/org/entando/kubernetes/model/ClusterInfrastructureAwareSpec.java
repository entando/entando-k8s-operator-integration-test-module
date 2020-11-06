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

import static java.util.Optional.ofNullable;

import io.fabric8.kubernetes.api.model.EnvVar;
import java.util.List;
import java.util.Optional;

public class ClusterInfrastructureAwareSpec extends KeycloakAwareSpec {

    private ResourceReference clusterInfrastructureToUse;

    public ClusterInfrastructureAwareSpec() {
    }

    public ClusterInfrastructureAwareSpec(String ingressHostName, String tlsSecretName, Integer replicas,
            DbmsVendor dbms, String serviceAccountToUse,
            List<EnvVar> environmentVariables,
            EntandoResourceRequirements resourceRequirements, KeycloakToUse keycloakToUse, ResourceReference clusterInfrastructureToUse) {
        super(ingressHostName, tlsSecretName, replicas, dbms, serviceAccountToUse, environmentVariables, resourceRequirements,
                keycloakToUse);

        this.clusterInfrastructureToUse = clusterInfrastructureToUse;
    }

    public Optional<ResourceReference> getClusterInfrastructureToUse() {
        return ofNullable(clusterInfrastructureToUse);
    }
}
