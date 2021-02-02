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

public abstract class ClusterInfrastructureAwareSpecFluent<N extends ClusterInfrastructureAwareSpecFluent<N>>
        extends KeycloakAwareSpecFluent<N> {

    protected ResourceReference clusterInfrastructureToUse;

    protected ClusterInfrastructureAwareSpecFluent(ClusterInfrastructureAwareSpec spec) {
        super(spec);
        this.clusterInfrastructureToUse = spec.getClusterInfrastructureToUse().orElse(null);
    }

    protected ClusterInfrastructureAwareSpecFluent() {
    }

    public N withClusterInfrastructureToUse(String namespace, String name) {
        this.clusterInfrastructureToUse = new ResourceReference(namespace, name);
        return thisAsF();
    }
}
