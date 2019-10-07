package org.entando.kubernetes.model.inprocesstest;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import org.entando.kubernetes.model.AbstractEntandoAppPluginLinkTest;
import org.entando.kubernetes.model.link.DoneableEntandoAppPluginLink;
import org.entando.kubernetes.model.link.EntandoAppPluginLink;
import org.junit.Rule;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;

@EnableRuleMigrationSupport
@Tag("in-process")
@SuppressWarnings("PMD.TestClassWithoutTestCases")
//Because PMD doesn't know they are inherited
public class EntandoAppPluginLinkMockedTest extends AbstractEntandoAppPluginLinkTest {

    @Rule
    public KubernetesServer server = new KubernetesServer(false, true);

    @Override
    protected DoneableEntandoAppPluginLink editEntandoAppPluginLink(EntandoAppPluginLink entandoPlugin) {
        return new DoneableEntandoAppPluginLink(entandoPlugin, builtEntandoAppPluginLink -> builtEntandoAppPluginLink);
    }

    @Override
    public KubernetesClient getClient() {
        return server.getClient();
    }

}
