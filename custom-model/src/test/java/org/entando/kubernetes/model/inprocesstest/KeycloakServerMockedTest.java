package org.entando.kubernetes.model.inprocesstest;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import org.entando.kubernetes.model.AbstractKeycloakServerTest;
import org.entando.kubernetes.model.keycloakserver.DoneableKeycloakServer;
import org.entando.kubernetes.model.keycloakserver.KeycloakServer;
import org.junit.Rule;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;

@EnableRuleMigrationSupport
@Tag("in-process")
@SuppressWarnings("PMD.TestClassWithoutTestCases")
//Because PMD doesn't know they are inherited
public class KeycloakServerMockedTest extends AbstractKeycloakServerTest {

    @Rule
    public KubernetesServer server = new KubernetesServer(false, true);

    @Override
    protected KubernetesClient getClient() {
        return this.server.getClient();
    }

    @Override
    protected DoneableKeycloakServer editKeycloakServer(KeycloakServer keycloakServer) {
        return new DoneableKeycloakServer(keycloakServer, builtKeycloakServer -> builtKeycloakServer);
    }

}
