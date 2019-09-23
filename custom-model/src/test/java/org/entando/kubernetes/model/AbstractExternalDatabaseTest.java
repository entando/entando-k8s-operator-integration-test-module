package org.entando.kubernetes.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.internal.CustomResourceOperationsImpl;
import java.util.List;
import org.entando.kubernetes.model.externaldatabase.DoneableExternalDatabase;
import org.entando.kubernetes.model.externaldatabase.ExternalDatabase;
import org.entando.kubernetes.model.externaldatabase.ExternalDatabaseBuilder;
import org.entando.kubernetes.model.externaldatabase.ExternalDatabaseList;
import org.entando.kubernetes.model.inprocesstest.ExternalDatabaseMockedTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class AbstractExternalDatabaseTest {

    protected static final String MY_NAMESPACE = "my-namespace";
    protected static final String MY_EXTERNAL_DATABASE = "my-external-database";
    private static final String MY_DB = "my_db";
    private static final String MYHOST_COM = "myhost.com";
    private static final int PORT_1521 = 1521;
    private static final String MY_DB_SECRET = "my-db-secret";
    private static CustomResourceDefinition externalDatabaseCrd;

    private static CustomResourceOperationsImpl<ExternalDatabase, ExternalDatabaseList,
            DoneableExternalDatabase> produceAllExternalDatabases(
            KubernetesClient client) {
        synchronized (ExternalDatabaseMockedTest.class) {
            externalDatabaseCrd = client.customResourceDefinitions().withName(ExternalDatabase.CRD_NAME).get();
            if (externalDatabaseCrd == null) {
                List<HasMetadata> list = client.load(Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("crd/ExternalDatabaseCRD.yaml")).get();
                externalDatabaseCrd = (CustomResourceDefinition) list.get(0);
                // see issue https://github.com/fabric8io/kubernetes-client/issues/1486
                externalDatabaseCrd.getSpec().getValidation().getOpenAPIV3Schema().setDependencies(null);
                client.customResourceDefinitions().create(externalDatabaseCrd);
            }

        }
        return (CustomResourceOperationsImpl<ExternalDatabase, ExternalDatabaseList, DoneableExternalDatabase>) client
                .customResources(externalDatabaseCrd, ExternalDatabase.class, ExternalDatabaseList.class, DoneableExternalDatabase.class);
    }

    @BeforeEach
    public void deleteExternalDatabase() throws InterruptedException {
        externalDatabases().inNamespace(MY_NAMESPACE).withName(MY_EXTERNAL_DATABASE).delete();
        while (externalDatabases().inNamespace(MY_NAMESPACE).list().getItems().size() > 0) {
            Thread.sleep(100);
        }
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
        getClient().namespaces().createOrReplaceWithNew().withNewMetadata().withName(MY_NAMESPACE).endMetadata().done();
        externalDatabases().inNamespace(MY_NAMESPACE).create(externalDatabase);
        //When
        ExternalDatabaseList list = externalDatabases().inNamespace(MY_NAMESPACE).list();
        ExternalDatabase actual = list.getItems().get(0);
        //Then
        assertThat(actual.getSpec().getDatabaseName(), is(MY_DB));
        assertThat(actual.getSpec().getHost(), is(MYHOST_COM));
        assertThat(actual.getSpec().getPort().get(), is(PORT_1521));
        assertThat(actual.getSpec().getDbms(), is(DbmsImageVendor.ORACLE));
        assertThat(actual.getSpec().getSecretName(), is(MY_DB_SECRET));
        assertThat(actual.getMetadata().getName(), is(MY_EXTERNAL_DATABASE));
    }

    protected abstract KubernetesClient getClient();

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
        getClient().namespaces().createOrReplaceWithNew().withNewMetadata().withName(MY_NAMESPACE).endMetadata().done();
        //When
        //We are not using the mock server here because of a known bug
        DoneableExternalDatabase doneableExternalDatabase = editExternalDatabase(externalDatabase);
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

    protected abstract DoneableExternalDatabase editExternalDatabase(ExternalDatabase externalDatabase);

    protected CustomResourceOperationsImpl<ExternalDatabase, ExternalDatabaseList, DoneableExternalDatabase> externalDatabases() {
        return produceAllExternalDatabases(
                getClient());
    }
}
