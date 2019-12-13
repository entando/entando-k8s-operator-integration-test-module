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

import static org.entando.kubernetes.model.externaldatabase.ExternalDatabaseOperationFactory.produceAllExternalDatabases;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import io.fabric8.kubernetes.client.dsl.internal.CustomResourceOperationsImpl;
import org.entando.kubernetes.model.externaldatabase.DoneableExternalDatabase;
import org.entando.kubernetes.model.externaldatabase.ExternalDatabase;
import org.entando.kubernetes.model.externaldatabase.ExternalDatabaseBuilder;
import org.entando.kubernetes.model.externaldatabase.ExternalDatabaseList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class AbstractExternalDatabaseTest implements CustomResourceTestUtil {

    protected static final String MY_EXTERNAL_DATABASE = "my-external-database";
    protected static final String MY_NAMESPACE = TestConfig.calculateNameSpace("my-namespace");
    private static final String MY_DB = "my_db";
    private static final String MYHOST_COM = "myhost.com";
    private static final int PORT_1521 = 1521;
    private static final String MY_DB_SECRET = "my-db-secret";

    @BeforeEach
    public void deleteExternalDatabase() {
        prepareNamespace(externalDatabases(), MY_NAMESPACE);
    }

    @Test
    public void testCreateExternalDatabase() {
        //Given
        ExternalDatabase externalDatabase = new ExternalDatabaseBuilder()
                .withNewMetadata().withName(MY_EXTERNAL_DATABASE)
                .withNamespace(MY_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .withDatabaseName(MY_DB)
                .withHost(MYHOST_COM)
                .withPort(PORT_1521)
                .withSecretName(MY_DB_SECRET)
                .withDbms(DbmsImageVendor.ORACLE)
                .endSpec()
                .build();
        externalDatabases().inNamespace(MY_NAMESPACE).createNew().withMetadata(externalDatabase.getMetadata())
                .withSpec(externalDatabase.getSpec()).done();
        //When
        ExternalDatabase actual = externalDatabases().inNamespace(MY_NAMESPACE).withName(MY_EXTERNAL_DATABASE).get();
        //Then
        assertThat(actual.getSpec().getDatabaseName(), is(MY_DB));
        assertThat(actual.getSpec().getHost(), is(MYHOST_COM));
        assertThat(actual.getSpec().getPort().get(), is(PORT_1521));
        assertThat(actual.getSpec().getDbms(), is(DbmsImageVendor.ORACLE));
        assertThat(actual.getSpec().getSecretName(), is(MY_DB_SECRET));
        assertThat(actual.getMetadata().getName(), is(MY_EXTERNAL_DATABASE));
    }

    @Test
    public void testEditExternalDatabase() {
        //Given
        ExternalDatabase externalDatabase = new ExternalDatabaseBuilder()
                .withNewMetadata().withName(MY_EXTERNAL_DATABASE)
                .withNamespace(MY_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .withDatabaseName("other_db")
                .withHost("otherhost.com")
                .withPort(5555)
                .withSecretName("othersecret")
                .withDbms(DbmsImageVendor.POSTGRESQL)
                .endSpec()
                .build();
        //When
        externalDatabases().inNamespace(MY_NAMESPACE).create(externalDatabase);
        DoneableExternalDatabase doneableExternalDatabase = externalDatabases().inNamespace(MY_NAMESPACE).withName(MY_EXTERNAL_DATABASE)
                .edit();
        ExternalDatabase actual = doneableExternalDatabase
                .editMetadata().addToLabels("my-label", "my-value")
                .endMetadata()
                .editSpec()
                .withDatabaseName(MY_DB)
                .withHost(MYHOST_COM)
                .withPort(PORT_1521)
                .withSecretName(MY_DB_SECRET)
                .withDbms(DbmsImageVendor.ORACLE)
                .endSpec()
                .withStatus(new WebServerStatus("some-qualifier"))
                .withStatus(new DbServerStatus("another-qualifier"))
                .withPhase(EntandoDeploymentPhase.STARTED)
                .done();
        //Then
        assertThat(actual.getSpec().getDatabaseName(), is(MY_DB));
        assertThat(actual.getSpec().getHost(), is(MYHOST_COM));
        assertThat(actual.getSpec().getPort().get(), is(PORT_1521));
        assertThat(actual.getSpec().getDbms(), is(DbmsImageVendor.ORACLE));
        assertThat(actual.getSpec().getSecretName(), is(MY_DB_SECRET));
        assertThat(actual.getMetadata().getLabels().get("my-label"), is("my-value"));
        assertThat("the status reflects", actual.getStatus().forServerQualifiedBy("some-qualifier").isPresent());
        assertThat("the status reflects", actual.getStatus().forDbQualifiedBy("another-qualifier").isPresent());
    }

    protected CustomResourceOperationsImpl<ExternalDatabase, ExternalDatabaseList, DoneableExternalDatabase> externalDatabases() {
        return produceAllExternalDatabases(getClient());
    }
}
