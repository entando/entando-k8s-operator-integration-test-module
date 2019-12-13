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

package org.entando.kubernetes.model.externaldatabase;

import org.entando.kubernetes.model.DbmsImageVendor;

public class EntandoExternalDatabaseSpecFluent<N extends EntandoExternalDatabaseSpecFluent> {

    private String databaseName;
    private DbmsImageVendor dbms;
    private String host;
    private Integer port;
    private String secretName;

    public EntandoExternalDatabaseSpecFluent(EntandoExternalDatabaseSpec spec) {
        this.databaseName = spec.getDatabaseName();
        this.dbms = spec.getDbms();
        this.host = spec.getHost();
        this.port = spec.getPort().orElse(null);
        this.secretName = spec.getSecretName();
    }

    public EntandoExternalDatabaseSpecFluent() {

    }

    public EntandoExternalDatabaseSpec build() {
        return new EntandoExternalDatabaseSpec(dbms, host, port, databaseName, secretName);
    }

    @SuppressWarnings("unchecked")
    public N withDatabaseName(String databaseName) {
        this.databaseName = databaseName;
        return (N) this;
    }

    @SuppressWarnings("unchecked")
    public N withDbms(DbmsImageVendor dbms) {
        this.dbms = dbms;
        return (N) this;
    }

    @SuppressWarnings("unchecked")
    public N withHost(String host) {
        this.host = host;
        return (N) this;
    }

    @SuppressWarnings("unchecked")
    public N withPort(Integer port) {
        this.port = port;
        return (N) this;
    }

    @SuppressWarnings("unchecked")
    public N withSecretName(String secretName) {
        this.secretName = secretName;
        return (N) this;
    }
}
