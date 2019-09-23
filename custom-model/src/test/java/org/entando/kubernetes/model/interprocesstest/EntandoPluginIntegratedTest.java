package org.entando.kubernetes.model.interprocesstest;

import io.fabric8.kubernetes.client.AutoAdaptableKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.entando.kubernetes.model.AbstractEntandoPluginTest;
import org.entando.kubernetes.model.plugin.DoneableEntandoPlugin;
import org.entando.kubernetes.model.plugin.EntandoPlugin;
import org.junit.jupiter.api.Tag;

@Tag("inter-process")
public class EntandoPluginIntegratedTest extends AbstractEntandoPluginTest {

    private final KubernetesClient client = new AutoAdaptableKubernetesClient();

    @Override
    protected DoneableEntandoPlugin editEntandoPlugin(EntandoPlugin entandoPlugin) {
        entandoPlugins().inNamespace(MY_NAMESPACE).create(entandoPlugin);
        return entandoPlugins().inNamespace(MY_NAMESPACE).withName(MY_PLUGIN).edit();
    }

    @Override
    protected KubernetesClient getClient() {
        return client;
    }

}
