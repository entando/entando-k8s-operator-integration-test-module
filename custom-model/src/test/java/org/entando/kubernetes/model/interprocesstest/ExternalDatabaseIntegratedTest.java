package org.entando.kubernetes.model.interprocesstest;

import io.fabric8.kubernetes.client.AutoAdaptableKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.entando.kubernetes.model.AbstractExternalDatabaseTest;
import org.entando.kubernetes.model.externaldatabase.DoneableExternalDatabase;
import org.entando.kubernetes.model.externaldatabase.ExternalDatabase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;

@EnableRuleMigrationSupport
@Tag("inter-process")
@SuppressWarnings("PMD.TestClassWithoutTestCases")
//Because PMD doesn't know they are inherited
public class ExternalDatabaseIntegratedTest extends AbstractExternalDatabaseTest {

    private final KubernetesClient client = new AutoAdaptableKubernetesClient();

    @Override
    protected KubernetesClient getClient() {
        return client;
    }

    @Override
    protected DoneableExternalDatabase editExternalDatabase(ExternalDatabase externalDatabase) {
        externalDatabases().inNamespace(MY_NAMESPACE).create(externalDatabase);
        return externalDatabases().inNamespace(MY_NAMESPACE).withName(MY_EXTERNAL_DATABASE).edit();
    }

}
