package org.entando.kubernetes.model.interprocesstest;

import io.fabric8.kubernetes.client.AutoAdaptableKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.entando.kubernetes.model.AbstractEntandoAppTest;
import org.entando.kubernetes.model.app.DoneableEntandoApp;
import org.entando.kubernetes.model.app.EntandoApp;
import org.junit.jupiter.api.Tag;

@Tag("inter-process")
@SuppressWarnings("PMD.TestClassWithoutTestCases")
//Because PMD doesn't know they are inherited
public class EntandoAppIntegratedTest extends AbstractEntandoAppTest {

    private final KubernetesClient client = new AutoAdaptableKubernetesClient();

    @Override
    protected DoneableEntandoApp editEntandoApp(EntandoApp entandoApp) throws InterruptedException {
        entandoApps().inNamespace(MY_NAMESPACE).create(entandoApp);
        return entandoApps().inNamespace(MY_NAMESPACE).withName(MY_APP).edit();
    }

    @Override
    public KubernetesClient getClient() {
        return client;
    }

}
