package org.entando.kubernetes.model.inprocesstest;

import io.fabric8.kubernetes.client.NamespacedKubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import org.entando.kubernetes.model.AbstractExternalDatabaseTest;
import org.entando.kubernetes.model.externaldatabase.DoneableExternalDatabase;
import org.entando.kubernetes.model.externaldatabase.ExternalDatabase;
import org.junit.Rule;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;

@EnableRuleMigrationSupport
@Tag("in-process")
public class ExternalDatabaseMockedTest extends AbstractExternalDatabaseTest {

    @Rule
    public KubernetesServer server = new KubernetesServer(false, true);

    @Override
    protected NamespacedKubernetesClient getClient() {
        return this.server.getClient();
    }

    @Override
    protected DoneableExternalDatabase editExternalDatabase(ExternalDatabase externalDatabase) {
        return new DoneableExternalDatabase(externalDatabase,
                builtExternalDatabase -> builtExternalDatabase);
    }

}
