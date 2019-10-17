/*
 *
 *  * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
 *  *
 *  * This library is free software; you can redistribute it and/or modify it under
 *  * the terms of the GNU Lesser General Public License as published by the Free
 *  * Software Foundation; either version 2.1 of the License, or (at your option)
 *  * any later version.
 *  *
 *  * This library is distributed in the hope that it will be useful, but WITHOUT
 *  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 *  * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 *  * details.
 *
 */

package org.entando.kubernetes.model.externaldatabase;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import java.util.Optional;
import org.entando.kubernetes.model.DbmsImageVendor;

@JsonSerialize
@JsonDeserialize
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
public class ExternalDatabaseSpec implements Serializable {

    private String dbms;
    private String host;
    private Integer port;
    private String databaseName;
    private String secretName;

    public ExternalDatabaseSpec() {
        super();
    }

    public ExternalDatabaseSpec(DbmsImageVendor dbms, String host, Integer port, String databaseName, String secretName) {
        this();
        this.dbms = dbms.toValue();
        this.host = host;
        this.secretName = secretName;
        this.port = port;
        this.databaseName = databaseName;
    }

    public DbmsImageVendor getDbms() {
        return DbmsImageVendor.forValue(dbms);
    }

    public String getHost() {
        return host;
    }

    public String getSecretName() {
        return secretName;
    }

    public Optional<Integer> getPort() {
        return Optional.ofNullable(port);
    }

    public String getDatabaseName() {
        return databaseName;
    }

}
