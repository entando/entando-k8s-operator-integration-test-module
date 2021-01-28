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

package org.entando.kubernetes.model.infrastructure;

import org.entando.kubernetes.model.KeycloakAwareSpecFluent;

public class EntandoClusterInfrastructureSpecFluent<F extends EntandoClusterInfrastructureSpecFluent<F>>
        extends KeycloakAwareSpecFluent<F> {

    private boolean isDefault;

    public EntandoClusterInfrastructureSpecFluent(EntandoClusterInfrastructureSpec spec) {
        super(spec);
        this.isDefault = spec.isDefault();
    }

    public EntandoClusterInfrastructureSpecFluent() {

    }

    @SuppressWarnings("unchecked")
    public F withDefault(boolean isDefault) {
        this.isDefault = isDefault;
        return (F) this;
    }

    public EntandoClusterInfrastructureSpec build() {
        return new EntandoClusterInfrastructureSpec(dbms, ingressHostName, tlsSecretName, replicas,
                keycloakToUse, isDefault, this.serviceAccountToUse, environmentVariables, resourceRequirements);
    }
}
