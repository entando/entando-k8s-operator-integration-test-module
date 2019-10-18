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

public class ExternalDatabaseSpecBuilder<N extends ExternalDatabaseSpecBuilder> {

    private String databaseName;
    private DbmsImageVendor dbms;
    private String host;
    private Integer port;
    private String secretName;

    public ExternalDatabaseSpecBuilder(ExternalDatabaseSpec instance) {
        this.databaseName = instance.getDatabaseName();
        this.dbms = instance.getDbms();
        this.host = instance.getHost();
        this.port = instance.getPort().orElse(null);
        this.secretName = instance.getSecretName();
    }

    public ExternalDatabaseSpecBuilder() {
        //Default constructor requried
    }

    public ExternalDatabaseSpec build() {
        return new ExternalDatabaseSpec(dbms, host, port, databaseName, secretName);
    }

    public N withDatabaseName(String databaseName) {
        this.databaseName = databaseName;
        return (N) this;
    }

    public N withDbms(DbmsImageVendor dbms) {
        this.dbms = dbms;
        return (N) this;
    }

    public N withHost(String host) {
        this.host = host;
        return (N) this;
    }

    public N withPort(Integer port) {
        this.port = port;
        return (N) this;
    }

    public N withSecretName(String secretName) {
        this.secretName = secretName;
        return (N) this;
    }
}
