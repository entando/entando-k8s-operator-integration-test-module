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

package org.entando.kubernetes.model.inprocesstest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.entando.kubernetes.model.DbmsImageVendor;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class DbmsImageVendorTest {

    @Tag("in-process")
    @Test
    public void test() {
        assertThat(DbmsImageVendor.MYSQL.getConnectionStringBuilder().onPort("1234").toHost("myhost.com").usingSchema("myschema")
                .usingDatabase("mydb")
                .buildConnectionString(), is("jdbc:mysql://myhost.com:1234/myschema"));
        assertThat(DbmsImageVendor.POSTGRESQL.getConnectionStringBuilder().onPort("1234").toHost("myhost.com").usingSchema("myschema")
                .usingDatabase("mydb")
                .buildConnectionString(), is("jdbc:postgresql://myhost.com:1234/mydb"));
        assertThat(DbmsImageVendor.ORACLE.getConnectionStringBuilder().onPort("1234").toHost("myhost.com").usingSchema("myschema")
                .usingDatabase("mydb")
                .buildConnectionString(), is("jdbc:oracle:thin:@//myhost.com:1234/mydb"));
        assertThat("the oracleMavenRepo config is present", DbmsImageVendor.ORACLE.getAdditionalConfig().stream().anyMatch(configVariable ->
                configVariable.getConfigKey().equals("oracleTablespace") && configVariable.getEnvironmentVariable()
                        .equals("TABLESPACE")));

    }
}
