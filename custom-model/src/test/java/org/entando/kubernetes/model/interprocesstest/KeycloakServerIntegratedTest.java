package org.entando.kubernetes.model.interprocesstest;

import io.fabric8.kubernetes.api.model.LoadBalancerStatus;
import io.fabric8.kubernetes.api.model.PodStatus;
import io.fabric8.kubernetes.api.model.ServiceStatus;
import io.fabric8.kubernetes.client.AutoAdaptableKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.entando.kubernetes.model.AbstractKeycloakServerTest;
import org.entando.kubernetes.model.DbServerStatus;
import org.entando.kubernetes.model.EntandoDeploymentPhase;
import org.entando.kubernetes.model.WebServerStatus;
import org.entando.kubernetes.model.keycloakserver.DoneableKeycloakServer;
import org.entando.kubernetes.model.keycloakserver.KeycloakServer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("inter-process")
public class KeycloakServerIntegratedTest extends AbstractKeycloakServerTest {

    private final KubernetesClient client = new AutoAdaptableKubernetesClient();

    @Override
    public KubernetesClient getClient() {
        return client;
    }

    @Override
    protected DoneableKeycloakServer editKeycloakServer(KeycloakServer keycloakServer) throws InterruptedException {
        keycloakServers().inNamespace(MY_NAMESPACE).create(keycloakServer);
        return keycloakServers().inNamespace(MY_NAMESPACE).withName(MY_KEYCLOAK).edit();
    }

    @Test
    public void multipleStatuses() throws InterruptedException {
        super.testCreateKeycloakServer();
        keycloakServers().inNamespace(MY_NAMESPACE).withName(MY_KEYCLOAK).edit().withPhase(EntandoDeploymentPhase.STARTED).done();
        DbServerStatus dbStatus = new DbServerStatus("db-qualifier");
        dbStatus.setPodStatus(new PodStatus());
        keycloakServers().inNamespace(MY_NAMESPACE).withName(MY_KEYCLOAK).edit().withStatus(dbStatus).done();
        WebServerStatus status1 = new WebServerStatus("server");
        keycloakServers().inNamespace(MY_NAMESPACE).withName(MY_KEYCLOAK).edit().withStatus(status1).done();
        status1.setServiceStatus(new ServiceStatus(new LoadBalancerStatus()));
        keycloakServers().inNamespace(MY_NAMESPACE).withName(MY_KEYCLOAK).edit().withStatus(status1).done();
    }

}
