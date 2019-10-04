package org.entando.kubernetes.model.inprocesstest;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import org.entando.kubernetes.model.AbstractEntandoClusterInfrastructureTest;
import org.entando.kubernetes.model.infrastructure.DoneableEntandoClusterInfrastructure;
import org.entando.kubernetes.model.infrastructure.EntandoClusterInfrastructure;
import org.junit.Rule;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;

@EnableRuleMigrationSupport
@Tag("in-process")
@SuppressWarnings("PMD.TestClassWithoutTestCases")
//Because PMD doesn't know they are inherited
public class EntandoClusterInfrastructureMockedTest extends AbstractEntandoClusterInfrastructureTest {

    @Rule
    public KubernetesServer server = new KubernetesServer(false, true);

    @Override
    public KubernetesClient getClient() {
        return this.server.getClient();
    }

    @Override
    protected DoneableEntandoClusterInfrastructure editEntandoClusterInfrastructure(EntandoClusterInfrastructure keycloakServer) {
        return new DoneableEntandoClusterInfrastructure(keycloakServer, entandoClusterInfrastructure -> entandoClusterInfrastructure);
    }

}
