package org.entando.kubernetes.model.interprocesstest;

import io.fabric8.kubernetes.client.AutoAdaptableKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.entando.kubernetes.model.AbstractEntandoAppPluginLinkTest;
import org.entando.kubernetes.model.link.DoneableEntandoAppPluginLink;
import org.entando.kubernetes.model.link.EntandoAppPluginLink;
import org.junit.jupiter.api.Tag;

@Tag("inter-process")
@SuppressWarnings("PMD.TestClassWithoutTestCases")
//Because PMD doesn't know they are inherited
public class EntandoAppPluginLinkIntegratedTest extends AbstractEntandoAppPluginLinkTest {

    private final KubernetesClient client = new AutoAdaptableKubernetesClient();

    @Override
    protected DoneableEntandoAppPluginLink editEntandoAppPluginLink(EntandoAppPluginLink entandoPlugin) throws InterruptedException {
        entandoAppPluginLinks().inNamespace(MY_APP_NAMESPACE).create(entandoPlugin);
        return entandoAppPluginLinks().inNamespace(MY_APP_NAMESPACE).withName(MY_PLUGIN).edit();
    }

    @Override
    public KubernetesClient getClient() {
        return client;
    }

}
