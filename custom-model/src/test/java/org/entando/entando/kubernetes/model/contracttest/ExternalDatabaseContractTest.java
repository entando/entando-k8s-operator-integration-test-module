package org.entando.entando.kubernetes.model.contracttest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.internal.CustomResourceOperationsImpl;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import java.util.List;
import org.entando.entando.kubernetes.model.DbmsImageVendor;
import org.entando.entando.kubernetes.model.externaldatabase.DoneableExternalDatabase;
import org.entando.entando.kubernetes.model.externaldatabase.ExternalDatabase;
import org.entando.entando.kubernetes.model.externaldatabase.ExternalDatabaseList;
import org.entando.entando.kubernetes.model.externaldatabase.ExternalDatabaseSpec;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;

@EnableRuleMigrationSupport
public class ExternalDatabaseContractTest {

    public static final String EXTERNALDATABASE_CRD_NAME = "externaldatabases.entando.org";
    private static CustomResourceDefinition externalDatabaseCrd;

    @Rule
    public KubernetesServer server = new KubernetesServer(false, true);

    private static CustomResourceOperationsImpl<ExternalDatabase, ExternalDatabaseList,
            DoneableExternalDatabase> produceAllExternalDatabases(
            KubernetesClient client) {
        synchronized (ExternalDatabaseContractTest.class) {
            externalDatabaseCrd = client.customResourceDefinitions().withName(EXTERNALDATABASE_CRD_NAME).get();
            if (externalDatabaseCrd == null) {
                List<HasMetadata> list = client.load(Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("crd/ExternalDatabaseCRD.yaml")).get();
                externalDatabaseCrd = (CustomResourceDefinition) list.get(0);
                // see issue https://github.com/fabric8io/kubernetes-client/issues/1486
                externalDatabaseCrd.getSpec().getValidation().getOpenAPIV3Schema().setDependencies(null);
                client.customResourceDefinitions().create(externalDatabaseCrd);
            }
        }
        return (CustomResourceOperationsImpl<ExternalDatabase, ExternalDatabaseList,
                DoneableExternalDatabase>) client
                .customResources(externalDatabaseCrd, ExternalDatabase.class, ExternalDatabaseList.class,
                        DoneableExternalDatabase.class);
    }

    @Test
    public void testPutExternalDatabase() {
        CustomResourceOperationsImpl<ExternalDatabase, ExternalDatabaseList, DoneableExternalDatabase> externalDatabases =
                externalDatabases();
        ExternalDatabase externalDatabase = new ExternalDatabase(
                new ExternalDatabaseSpec(DbmsImageVendor.ORACLE, "myhost!-534,mqcom", 1521, "my_db", "my-db-secret"));
        externalDatabase.getMetadata().setName("my-external-database");
        externalDatabase.getMetadata().setNamespace("my-namespace");
        server.getClient().namespaces().createNew().withNewMetadata().withName("my-namespace").endMetadata().done();
        externalDatabases().create(externalDatabase);
        ExternalDatabaseList list = externalDatabases.inNamespace("my-namespace").list();
        ExternalDatabase actual = list.getItems().get(0);
        assertThat(actual.getSpec().getDatabaseName(), is(externalDatabase.getSpec().getDatabaseName()));
    }

    private CustomResourceOperationsImpl<ExternalDatabase, ExternalDatabaseList, DoneableExternalDatabase> externalDatabases() {
        return produceAllExternalDatabases(
                server.getClient());
    }

}
