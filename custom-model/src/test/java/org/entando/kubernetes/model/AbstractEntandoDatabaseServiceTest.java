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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.Collections;
import org.entando.kubernetes.model.capability.CapabilityScope;
import org.entando.kubernetes.model.common.DbmsVendor;
import org.entando.kubernetes.model.common.EntandoDeploymentPhase;
import org.entando.kubernetes.model.common.ServerStatus;
import org.entando.kubernetes.model.externaldatabase.EntandoDatabaseService;
import org.entando.kubernetes.model.externaldatabase.EntandoDatabaseServiceBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//Sonar doesn't pick up that this class is extended in other packages
@SuppressWarnings("java:S5786")
public abstract class AbstractEntandoDatabaseServiceTest implements CustomResourceTestUtil {

    public static final String MY_PARAM_VALUE = "my-param-value";
    public static final String MY_PARAM = "my-param";
    public static final String MY_TABLESPACE = "my_tablespace";
    protected static final String MY_EXTERNAL_DATABASE = "my-external-database";
    protected static final String MY_NAMESPACE = TestConfig.calculateNameSpace("my-namespace");
    private static final String MY_DB = "my_db";
    private static final String MYHOST_COM = "myhost.com";
    private static final int PORT_1521 = 1521;
    private static final String MY_DB_SECRET = "my-db-secret";
    private static final String MY_STORAGE_CLASS = "my-storage-class";

    @BeforeEach
    public void deleteEntandoDatabaseService() {
        prepareNamespace(getClient().customResources(EntandoDatabaseService.class), MY_NAMESPACE);
    }

    @Test
    void testCreateEntandoDatabaseService() {
        //Given
        EntandoDatabaseService externalDatabase = new EntandoDatabaseServiceBuilder()
                .withNewMetadata().withName(MY_EXTERNAL_DATABASE)
                .withNamespace(MY_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .withDatabaseName(MY_DB)
                .withHost(MYHOST_COM)
                .withPort(PORT_1521)
                .withTablespace(MY_TABLESPACE)
                .withSecretName(MY_DB_SECRET)
                .withStorageClass(MY_STORAGE_CLASS)
                .addToJdbcParameters(MY_PARAM, MY_PARAM_VALUE)
                .withDbms(DbmsVendor.ORACLE)
                .withCreateDeployment(true)
                .withProvidedCapabilityScope(CapabilityScope.CLUSTER)
                .endSpec()
                .build();
        getClient().customResources(EntandoDatabaseService.class).inNamespace(MY_NAMESPACE).create(externalDatabase);
        //When
        EntandoDatabaseService actual = getClient().customResources(EntandoDatabaseService.class).inNamespace(MY_NAMESPACE)
                .withName(MY_EXTERNAL_DATABASE).get();
        //Then
        assertThat(actual.getSpec().getDatabaseName().get(), is(MY_DB));
        assertThat(actual.getSpec().getHost().get(), is(MYHOST_COM));
        assertThat(actual.getSpec().getPort().get(), is(PORT_1521));
        assertThat(actual.getSpec().getDbms().get(), is(DbmsVendor.ORACLE));
        assertThat(actual.getSpec().getCreateDeployment().get(), is(true));
        assertThat(actual.getSpec().getTablespace().get(), is(MY_TABLESPACE));
        assertThat(actual.getSpec().getSecretName().get(), is(MY_DB_SECRET));
        assertThat(actual.getSpec().getStorageClass().get(), is(MY_STORAGE_CLASS));
        assertThat(actual.getSpec().getProvidedCapabilityScope().get(), is(CapabilityScope.CLUSTER));
        assertThat(actual.getSpec().getJdbcParameters().get(MY_PARAM), is(MY_PARAM_VALUE));
        assertThat(actual.getMetadata().getName(), is(MY_EXTERNAL_DATABASE));
    }

    @Test
    void testEditEntandoDatabaseService() {
        //Given
        EntandoDatabaseService externalDatabase = new EntandoDatabaseServiceBuilder()
                .withNewMetadata().withName(MY_EXTERNAL_DATABASE)
                .withNamespace(MY_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .withDatabaseName("other_db")
                .withStorageClass("another-storage-class")
                .withHost("otherhost.com")
                .withTablespace(MY_TABLESPACE)
                .withJdbcParameters(Collections.singletonMap("asdfasdf", "afafafaf"))
                .withPort(5555)
                .withSecretName("othersecret")
                .withProvidedCapabilityScope(CapabilityScope.NAMESPACE)
                .withDbms(DbmsVendor.POSTGRESQL)
                .withCreateDeployment(false)
                .endSpec()
                .build();
        //When
        final EntandoDatabaseServiceBuilder toEdit = new EntandoDatabaseServiceBuilder(
                getClient().customResources(EntandoDatabaseService.class).inNamespace(MY_NAMESPACE).create(externalDatabase));
        EntandoDatabaseService actual = getClient().customResources(EntandoDatabaseService.class).inNamespace(MY_NAMESPACE)
                .withName(MY_EXTERNAL_DATABASE).patch(
                        toEdit
                                .editMetadata().addToLabels("my-label", "my-value")
                                .endMetadata()
                                .editSpec()
                                .withDatabaseName(MY_DB)
                                .withHost(MYHOST_COM)
                                .withPort(PORT_1521)
                                .withTablespace(MY_TABLESPACE)
                                .withJdbcParameters(Collections.singletonMap(MY_PARAM, MY_PARAM_VALUE))
                                .withStorageClass(MY_STORAGE_CLASS)
                                .withSecretName(MY_DB_SECRET)
                                .withCreateDeployment(true)
                                .withProvidedCapabilityScope(CapabilityScope.CLUSTER)
                                .withDbms(DbmsVendor.ORACLE)
                                .endSpec()
                                .build());
        actual.getStatus().putServerStatus(new ServerStatus("some-qualifier"));
        actual.getStatus().putServerStatus(new ServerStatus("some-other-qualifier"));
        actual.getStatus().putServerStatus(new ServerStatus("some-qualifier"));
        actual.getStatus().putServerStatus(new ServerStatus("another-qualifier"));
        actual.getStatus().updateDeploymentPhase(EntandoDeploymentPhase.STARTED, actual.getMetadata().getGeneration());

        //Then
        assertThat(actual.getSpec().getDatabaseName().get(), is(MY_DB));
        assertThat(actual.getSpec().getHost().get(), is(MYHOST_COM));
        assertThat(actual.getSpec().getPort().get(), is(PORT_1521));
        assertThat(actual.getSpec().getDbms().get(), is(DbmsVendor.ORACLE));
        assertThat(actual.getSpec().getCreateDeployment().get(), is(true));
        assertThat(actual.getSpec().getJdbcParameters().get(MY_PARAM), is(MY_PARAM_VALUE));
        assertThat(actual.getSpec().getJdbcParameters().get("asdfasdf"), is(nullValue()));
        assertThat(actual.getSpec().getSecretName().get(), is(MY_DB_SECRET));
        assertThat(actual.getSpec().getStorageClass().get(), is(MY_STORAGE_CLASS));
        assertThat(actual.getSpec().getTablespace().get(), is(MY_TABLESPACE));
        assertThat(actual.getSpec().getProvidedCapabilityScope().get(), is(CapabilityScope.CLUSTER));
        assertThat(actual.getMetadata().getLabels().get("my-label"), is("my-value"));
        assertThat("the status reflects", actual.getStatus().getServerStatus("some-qualifier").isPresent());
        assertThat("the status reflects", actual.getStatus().getServerStatus("another-qualifier").isPresent());
    }

}