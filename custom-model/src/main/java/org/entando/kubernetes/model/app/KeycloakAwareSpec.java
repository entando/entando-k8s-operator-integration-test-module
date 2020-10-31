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

package org.entando.kubernetes.model.app;

import static java.util.Optional.ofNullable;

import io.fabric8.kubernetes.api.model.EnvVar;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.entando.kubernetes.model.DbmsVendor;
import org.entando.kubernetes.model.EntandoDeploymentSpec;
import org.entando.kubernetes.model.EntandoResourceRequirements;
import org.entando.kubernetes.model.KeycloakToUse;

public abstract class KeycloakAwareSpec extends EntandoDeploymentSpec {

    protected KeycloakToUse keycloakToUse;

    public KeycloakAwareSpec() {
        super();
    }

    public KeycloakAwareSpec(
            String ingressHostName,
            String tlsSecretName,
            Integer replicas,
            DbmsVendor dbms,
            String serviceAccountToUse,
            Map<String, String> parameters,
            List<EnvVar> environmentVariables,
            EntandoResourceRequirements resourceRequirements,
            KeycloakToUse keycloakToUse) {
        super(ingressHostName, tlsSecretName, replicas, dbms, serviceAccountToUse, parameters, environmentVariables, resourceRequirements);
        this.keycloakToUse = keycloakToUse;
    }

    public Optional<KeycloakToUse> getKeycloakToUse() {
        return ofNullable(keycloakToUse);
    }
}
