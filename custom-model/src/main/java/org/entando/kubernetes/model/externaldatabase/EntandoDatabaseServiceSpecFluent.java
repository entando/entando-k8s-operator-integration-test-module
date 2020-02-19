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

import static org.entando.kubernetes.model.Coalescence.coalesce;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.entando.kubernetes.model.DbmsVendor;

public abstract class EntandoDatabaseServiceSpecFluent<N extends EntandoDatabaseServiceSpecFluent> {

    private String databaseName;
    private DbmsVendor dbms;
    private String host;
    private Integer port;
    private String secretName;
    private Map<String, String> parameters = new ConcurrentHashMap<>();

    public EntandoDatabaseServiceSpecFluent(EntandoDatabaseServiceSpec spec) {
        this.databaseName = spec.getDatabaseName();
        this.dbms = spec.getDbms();
        this.host = spec.getHost();
        this.port = spec.getPort().orElse(null);
        this.secretName = spec.getSecretName();
        this.parameters = coalesce(spec.getParameters(), this.parameters);
    }

    public EntandoDatabaseServiceSpecFluent() {

    }

    public EntandoDatabaseServiceSpec build() {
        return new EntandoDatabaseServiceSpec(dbms, host, port, databaseName, secretName, parameters);
    }

    public N withDatabaseName(String databaseName) {
        this.databaseName = databaseName;
        return thisAsN();
    }

    public N withDbms(DbmsVendor dbms) {
        this.dbms = dbms;
        return thisAsN();
    }

    public N withHost(String host) {
        this.host = host;
        return thisAsN();
    }

    public N withPort(Integer port) {
        this.port = port;
        return thisAsN();
    }

    public N withSecretName(String secretName) {
        this.secretName = secretName;
        return thisAsN();
    }

    public N withParameters(Map<String, String> parameters) {
        this.parameters = new ConcurrentHashMap<>(parameters);
        return thisAsN();
    }

    public N addToParameters(String name, String value) {
        this.parameters.put(name, value);
        return thisAsN();
    }

    @SuppressWarnings("unchecked")
    protected N thisAsN() {
        return (N) this;
    }
}
