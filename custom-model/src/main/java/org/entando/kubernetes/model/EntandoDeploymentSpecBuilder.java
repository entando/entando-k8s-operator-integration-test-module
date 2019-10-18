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

public abstract class EntandoDeploymentSpecBuilder<N extends EntandoDeploymentSpecBuilder> {

    protected DbmsImageVendor dbms;
    protected String ingressHostName;
    protected String tlsSecretName;
    protected Integer replicas = 1;

    protected EntandoDeploymentSpecBuilder(EntandoDeploymentSpec spec) {
        this.dbms = spec.getDbms().orElse(null);
        this.ingressHostName = spec.getIngressHostName().orElse(null);
        this.replicas = spec.getReplicas().orElse(null);
        this.tlsSecretName = spec.getTlsSecretName().orElse(null);
    }

    protected EntandoDeploymentSpecBuilder() {
    }

    public final N withDbms(DbmsImageVendor dbms) {
        this.dbms = dbms;
        return (N) this;
    }

    public final N withTlsSecretName(String tlsSecretName) {
        this.tlsSecretName = tlsSecretName;
        return (N) this;
    }

    public final N withReplicas(int replicas) {
        this.replicas = replicas;
        return (N) this;
    }

    public final N withIngressHostName(String ingressHostName) {
        this.ingressHostName = ingressHostName;
        return (N) this;
    }
}
