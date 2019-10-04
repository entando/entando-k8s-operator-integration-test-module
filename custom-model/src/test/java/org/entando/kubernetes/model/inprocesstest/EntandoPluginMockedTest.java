package org.entando.kubernetes.model.inprocesstest;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import org.entando.kubernetes.model.AbstractEntandoPluginTest;
import org.entando.kubernetes.model.plugin.DoneableEntandoPlugin;
import org.entando.kubernetes.model.plugin.EntandoPlugin;
import org.junit.Rule;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;

@EnableRuleMigrationSupport
@Tag("in-process")
@SuppressWarnings("PMD.TestClassWithoutTestCases")
//Because PMD doesn't know they are inherited
public class EntandoPluginMockedTest extends AbstractEntandoPluginTest {

    @Rule
    public KubernetesServer server = new KubernetesServer(false, true);

    @Override
    protected DoneableEntandoPlugin editEntandoPlugin(EntandoPlugin entandoPlugin) {
        return new DoneableEntandoPlugin(entandoPlugin, builtEntandoPlugin -> builtEntandoPlugin);
    }

    @Override
    public KubernetesClient getClient() {
        return server.getClient();
    }

}
