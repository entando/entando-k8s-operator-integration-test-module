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

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.entando.kubernetes.model.DbmsVendor;
import org.entando.kubernetes.model.EntandoDeploymentSpecBuilder;

public abstract class EntandoDatabaseServiceSpecFluent<N extends EntandoDatabaseServiceSpecFluent> extends EntandoDeploymentSpecBuilder<N> {

    private String databaseName;
    private DbmsVendor dbms;
    private String host;
    private Integer port;
    private String secretName;
    private Map<String, String> jdbcParameters;
    private String tablespace;
    private Boolean createDeployment;

    protected EntandoDatabaseServiceSpecFluent(EntandoDatabaseServiceSpec spec) {
        super(spec);
        this.databaseName = spec.getDatabaseName().orElse(null);
        this.dbms = spec.getDbms();
        this.host = spec.getHost().orElse(null);
        this.port = spec.getPort().orElse(null);
        this.secretName = spec.getSecretName().orElse(null);
        this.tablespace = spec.getTablespace().orElse(null);
        this.createDeployment = spec.getCreateDeployment().orElse(null);
        this.jdbcParameters = coalesce(spec.getJdbcParameters(), this.jdbcParameters);
    }

    protected EntandoDatabaseServiceSpecFluent() {

    }

    public EntandoDatabaseServiceSpec build() {
        return new EntandoDatabaseServiceSpec(
                dbms,
                host,
                port,
                databaseName,
                tablespace,
                secretName,
                createDeployment,
                jdbcParameters,
                replicas,
                serviceAccountToUse,
                environmentVariables,
                resourceRequirements);
    }

    public N withDatabaseName(String databaseName) {
        this.databaseName = databaseName;
        return thisAsN();
    }

    public N withCreateDeployment(Boolean createDeployment) {
        this.createDeployment = createDeployment;
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

    public N withJdbcParameters(Map<String, String> jdbcParameters) {
        this.jdbcParameters = new ConcurrentHashMap<>(jdbcParameters);
        return thisAsN();
    }

    public N addToJdbcParameters(String name, String value) {
        if (jdbcParameters == null) {
            jdbcParameters = new ConcurrentHashMap<>();
        }
        this.jdbcParameters.put(name, value);
        return thisAsN();
    }

    public N withTablespace(String tablespace) {
        this.tablespace = tablespace;
        return thisAsN();
    }

}
