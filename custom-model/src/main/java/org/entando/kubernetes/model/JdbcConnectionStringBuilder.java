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

package org.entando.kubernetes.model;

public abstract class JdbcConnectionStringBuilder {

    private String host;
    private String port;
    private String database;
    private String schema;

    public JdbcConnectionStringBuilder toHost(String host) {
        this.host = host;
        return this;
    }

    public JdbcConnectionStringBuilder onPort(String port) {
        this.port = port;
        return this;
    }

    public JdbcConnectionStringBuilder usingDatabase(String database) {
        this.database = database;
        return this;
    }

    public JdbcConnectionStringBuilder usingSchema(String schema) {
        this.schema = schema;
        return this;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public String getSchema() {
        return schema;
    }

    public abstract String buildConnectionString();

}
