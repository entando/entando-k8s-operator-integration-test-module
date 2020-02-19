package org.entando.kubernetes.controller.keycloakserver.interprocesstests;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.fabric8.kubernetes.api.model.DoneablePod;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.PodResource;
import java.util.concurrent.TimeUnit;
import org.entando.kubernetes.controller.EntandoOperatorConfig;
import org.entando.kubernetes.controller.EntandoOperatorConfigProperty;
import org.entando.kubernetes.controller.KubeUtils;
import org.entando.kubernetes.controller.common.TlsHelper;
import org.entando.kubernetes.controller.integrationtest.support.ClusterInfrastructureIntegrationTestHelper;
import org.entando.kubernetes.controller.integrationtest.support.EntandoAppIntegrationTestHelper;
import org.entando.kubernetes.controller.integrationtest.support.EntandoOperatorTestConfig;
import org.entando.kubernetes.controller.integrationtest.support.EntandoOperatorTestConfig.TestTarget;
import org.entando.kubernetes.controller.integrationtest.support.EntandoPluginIntegrationTestHelper;
import org.entando.kubernetes.controller.integrationtest.support.FluentIntegrationTesting;
import org.entando.kubernetes.controller.integrationtest.support.HttpTestHelper;
import org.entando.kubernetes.controller.integrationtest.support.K8SIntegrationTestHelper;
import org.entando.kubernetes.controller.integrationtest.support.KeycloakIntegrationTestHelper;
import org.entando.kubernetes.controller.integrationtest.support.TestFixturePreparation;
import org.entando.kubernetes.controller.keycloakserver.EntandoKeycloakServerController;
import org.entando.kubernetes.model.app.EntandoApp;
import org.entando.kubernetes.model.externaldatabase.EntandoDatabaseService;
import org.entando.kubernetes.model.infrastructure.EntandoClusterInfrastructure;
import org.entando.kubernetes.model.keycloakserver.EntandoKeycloakServer;
import org.entando.kubernetes.model.plugin.EntandoPlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class AddEntandoKeycloakServerBaseIT implements FluentIntegrationTesting {

    public static final int KEYCLOAK_DB_PORT = 5432;
    protected K8SIntegrationTestHelper helper = new K8SIntegrationTestHelper();
    protected KubernetesClient client;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void cleanup() {
        System.setProperty(EntandoOperatorConfigProperty.ENTANDO_K8S_OPERATOR_NAMESPACE_TO_OBSERVE.getJvmSystemProperty(),
                TestFixturePreparation.ENTANDO_CONTROLLERS_NAMESPACE);
        //Reset all namespaces as they depend on previously created Keycloak clients that are now invalid
        helper.setTextFixture(deleteAll(EntandoKeycloakServer.class)
                .fromNamespace(KeycloakIntegrationTestHelper.KEYCLOAK_NAMESPACE)
                .deleteAll(EntandoDatabaseService.class)
                .fromNamespace(KeycloakIntegrationTestHelper.KEYCLOAK_NAMESPACE)
                .deleteAll(EntandoClusterInfrastructure.class)
                .fromNamespace(ClusterInfrastructureIntegrationTestHelper.CLUSTER_INFRASTRUCTURE_NAMESPACE)
                .deleteAll(EntandoApp.class)
                .fromNamespace(EntandoAppIntegrationTestHelper.TEST_NAMESPACE)
                .deleteAll(EntandoPlugin.class)
                .fromNamespace(EntandoPluginIntegrationTestHelper.TEST_PLUGIN_NAMESPACE)
        );
        client = helper.getClient();
        await().atMost(2, TimeUnit.MINUTES).ignoreExceptions().pollInterval(10, TimeUnit.SECONDS).until(this::killPgPod);
        EntandoKeycloakServerController controller = new EntandoKeycloakServerController(client, false);
        if (EntandoOperatorTestConfig.getTestTarget() == TestTarget.K8S) {
            helper.keycloak().listenAndRespondWithImageVersionUnderTest(KeycloakIntegrationTestHelper.KEYCLOAK_NAMESPACE);
        } else {
            helper.keycloak().listenAndRespondWithStartupEvent(KeycloakIntegrationTestHelper.KEYCLOAK_NAMESPACE, controller::onStartup);
        }
    }

    private boolean killPgPod() {
        PodResource<Pod, DoneablePod> resource = client.pods()
                .inNamespace(KeycloakIntegrationTestHelper.KEYCLOAK_NAMESPACE).withName("pg-test");
        if (resource.fromServer().get() == null) {
            return true;
        }
        resource.delete();
        return false;
    }

    @AfterEach
    public void afterwards() {
        helper.afterTest();
        client.close();
    }

    protected void verifyKeycloakDeployment() {
        String http = TlsHelper.getDefaultProtocol();
        await().atMost(15, TimeUnit.SECONDS).pollInterval(1, TimeUnit.SECONDS).ignoreExceptions().until(() -> HttpTestHelper
                .statusOk(http + "://" + KeycloakIntegrationTestHelper.KEYCLOAK_NAME + "." + helper.getDomainSuffix()
                        + "/auth"));
        Deployment deployment = client.apps().deployments().inNamespace(KeycloakIntegrationTestHelper.KEYCLOAK_NAMESPACE)
                .withName(KeycloakIntegrationTestHelper.KEYCLOAK_NAME + "-server-deployment").get();
        assertThat(thePortNamed("server-port")
                        .on(theContainerNamed("server-container").on(deployment))
                        .getContainerPort(),
                is(8080));
        Service service = client.services().inNamespace(KeycloakIntegrationTestHelper.KEYCLOAK_NAMESPACE).withName(
                KeycloakIntegrationTestHelper.KEYCLOAK_NAME + "-server-service").get();
        assertThat(thePortNamed("server-port").on(service).getPort(), is(8080));
        assertTrue(deployment.getStatus().getReadyReplicas() >= 1);
        assertTrue(helper.keycloak().getOperations()
                .inNamespace(KeycloakIntegrationTestHelper.KEYCLOAK_NAMESPACE).withName(KeycloakIntegrationTestHelper.KEYCLOAK_NAME)
                .fromServer().get().getStatus().forServerQualifiedBy("server").isPresent());

        Secret adminSecret = client.secrets()
                .inNamespace(client.getNamespace())
                .withName(EntandoOperatorConfig.getDefaultKeycloakSecretName())
                .get();
        assertNotNull(adminSecret);
        assertTrue(adminSecret.getData().containsKey(KubeUtils.USERNAME_KEY));
        assertTrue(adminSecret.getData().containsKey(KubeUtils.PASSSWORD_KEY));
        assertTrue(adminSecret.getData().containsKey(KubeUtils.URL_KEY));
    }
}
