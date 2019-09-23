package org.entando.kubernetes.model.interprocesstest;

import io.fabric8.kubernetes.client.AutoAdaptableKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.entando.kubernetes.model.AbstractKeycloakServerTest;
import org.entando.kubernetes.model.keycloakserver.DoneableKeycloakServer;
import org.entando.kubernetes.model.keycloakserver.KeycloakServer;
import org.junit.jupiter.api.Tag;

@Tag("inter-process")
public class KeycloakServerIntegratedTest extends AbstractKeycloakServerTest {

    private final KubernetesClient client = new AutoAdaptableKubernetesClient();

    @Override
    protected KubernetesClient getClient() {
        return client;
    }

    @Override
    protected DoneableKeycloakServer editKeycloakServer(KeycloakServer keycloakServer) {
        keycloakServers().inNamespace(MY_NAMESPACE).create(keycloakServer);
        return keycloakServers().inNamespace(MY_NAMESPACE).withName(MY_KEYCLOAK).edit();
    }

}
