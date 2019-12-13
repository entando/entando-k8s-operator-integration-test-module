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

import static org.entando.kubernetes.model.externaldatabase.EntandoExternalDatabaseOperationFactory.produceAllEntandoExternalDatabases;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.entando.kubernetes.model.externaldatabase.DoneableEntandoExternalDatabase;
import org.entando.kubernetes.model.externaldatabase.EntandoExternalDatabase;
import org.entando.kubernetes.model.externaldatabase.EntandoExternalDatabaseBuilder;
import org.entando.kubernetes.model.externaldatabase.EntandoExternalDatabaseList;
import io.fabric8.kubernetes.client.dsl.internal.CustomResourceOperationsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class AbstractEntandoExternalDatabaseTest implements CustomResourceTestUtil {

    protected static final String MY_EXTERNAL_DATABASE = "my-external-database";
    protected static final String MY_NAMESPACE = TestConfig.calculateNameSpace("my-namespace");
    private static final String MY_DB = "my_db";
    private static final String MYHOST_COM = "myhost.com";
    private static final int PORT_1521 = 1521;
    private static final String MY_DB_SECRET = "my-db-secret";

    @BeforeEach
    public void deleteEntandoExternalDatabase() {
        prepareNamespace(externalDatabases(), MY_NAMESPACE);
    }

    @Test
    public void testCreateEntandoExternalDatabase() {
        //Given
        EntandoExternalDatabase externalDatabase = new EntandoExternalDatabaseBuilder()
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
        getClient().namespaces().createOrReplaceWithNew().withNewMetadata().withName(MY_NAMESPACE).endMetadata().done();
        externalDatabases().inNamespace(MY_NAMESPACE).createNew().withMetadata(externalDatabase.getMetadata())
                .withSpec(externalDatabase.getSpec()).done();
        //When
        EntandoExternalDatabase actual = externalDatabases().inNamespace(MY_NAMESPACE).withName(MY_EXTERNAL_DATABASE).get();
        //Then
        assertThat(actual.getSpec().getDatabaseName(), is(MY_DB));
        assertThat(actual.getSpec().getHost(), is(MYHOST_COM));
        assertThat(actual.getSpec().getPort().get(), is(PORT_1521));
        assertThat(actual.getSpec().getDbms(), is(DbmsImageVendor.ORACLE));
        assertThat(actual.getSpec().getSecretName(), is(MY_DB_SECRET));
        assertThat(actual.getMetadata().getName(), is(MY_EXTERNAL_DATABASE));
    }

    @Test
    public void testEditEntandoExternalDatabase() {
        //Given
        EntandoExternalDatabase externalDatabase = new EntandoExternalDatabaseBuilder()
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
        getClient().namespaces().createOrReplaceWithNew().withNewMetadata().withName(MY_NAMESPACE).endMetadata().done();
        //When
        //We are not using the mock server here because of a known bug
        DoneableEntandoExternalDatabase doneableEntandoExternalDatabase = editEntandoExternalDatabase(externalDatabase);
        EntandoExternalDatabase actual = doneableEntandoExternalDatabase
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

    protected DoneableEntandoExternalDatabase editEntandoExternalDatabase(EntandoExternalDatabase externalDatabase) {
        externalDatabases().inNamespace(MY_NAMESPACE).create(externalDatabase);
        return externalDatabases().inNamespace(MY_NAMESPACE).withName(MY_EXTERNAL_DATABASE).edit();
    }

    protected CustomResourceOperationsImpl<EntandoExternalDatabase, EntandoExternalDatabaseList, DoneableEntandoExternalDatabase> externalDatabases() {
        return produceAllEntandoExternalDatabases(getClient());
    }
}
