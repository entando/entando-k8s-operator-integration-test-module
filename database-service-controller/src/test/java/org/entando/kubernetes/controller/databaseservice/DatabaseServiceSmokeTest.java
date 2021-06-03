package org.entando.kubernetes.controller.databaseservice;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.LocalPortForward;
import io.fabric8.kubernetes.client.Watcher.Action;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import org.entando.kubernetes.controller.spi.common.LabelNames;
import org.entando.kubernetes.controller.spi.common.NameUtils;
import org.entando.kubernetes.controller.spi.common.ResourceUtils;
import org.entando.kubernetes.controller.spi.common.SecretUtils;
import org.entando.kubernetes.controller.support.client.impl.DefaultSimpleK8SClient;
import org.entando.kubernetes.controller.support.client.impl.EntandoOperatorTestConfig;
import org.entando.kubernetes.controller.support.client.impl.integrationtesthelpers.FluentIntegrationTesting;
import org.entando.kubernetes.controller.support.client.impl.integrationtesthelpers.TestFixturePreparation;
import org.entando.kubernetes.model.common.DbmsVendor;
import org.entando.kubernetes.model.externaldatabase.EntandoDatabaseService;
import org.entando.kubernetes.model.externaldatabase.EntandoDatabaseServiceBuilder;
import org.entando.kubernetes.test.e2etest.ControllerExecutor;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.postgresql.util.Base64;

@Tags({@Tag("smoke")})
@Feature("As an Entando Operator users, I want to use a Docker container to process an EntandoDatabaseService so that I don't need to "
        + "know any of its implementation details to use it.")
class DatabaseServiceSmokeTest implements FluentIntegrationTesting {

    private static final String MY_NAMESPACE = EntandoOperatorTestConfig.calculateNameSpace("my-namespace");
    private EntandoDatabaseService entandoDatabaseService;

    @Test
    @Description("Should deploy PostgreSQL successfully")
    void testDeployment() throws SQLException {
        KubernetesClient client = new DefaultKubernetesClient();
        final DefaultSimpleK8SClient simpleClient = new DefaultSimpleK8SClient(client);
        step("Given I have a clean namespace", () -> {
            TestFixturePreparation.prepareTestFixture(client, deleteAll(EntandoDatabaseService.class).fromNamespace(MY_NAMESPACE));
        });
        step("And I have created an EntandoDatabaseService custom resource", () -> {
            this.entandoDatabaseService = simpleClient.entandoResources()
                    .createOrPatchEntandoResource(
                            new EntandoDatabaseServiceBuilder()
                                    .withNewMetadata()
                                    .withNamespace(MY_NAMESPACE)
                                    .withName("my-db-service")
                                    .endMetadata()
                                    .withNewSpec()
                                    .withCreateDeployment(true)
                                    .withDatabaseName("my_db")
                                    .withDbms(DbmsVendor.POSTGRESQL)
                                    .endSpec()
                                    .build()
                    );
        });
        step("When I run the entando-k8s-database-service-controller container against the EntandoDatabaseService", () -> {
            ControllerExecutor executor = new ControllerExecutor(MY_NAMESPACE, simpleClient,
                    r -> "entando-k8s-database-service-controller");
            executor.runControllerFor(Action.ADDED, entandoDatabaseService,
                    EntandoOperatorTestConfig.getVersionOfImageUnderTest().orElse("0.0.0-SNAPSHOT-8"));
        });
        step("Then I can connect directly to the database service on the newly deployed pod", () -> {
            final Map<String, String> labels = ResourceUtils.labelsFromResource(entandoDatabaseService);
            labels.put(LabelNames.DEPLOYMENT.getName(), "my-db-service");
            Pod dbPod = client.pods()
                    .inNamespace(MY_NAMESPACE)
                    .withLabels(labels)
                    .list()
                    .getItems()
                    .get(0);
            LocalPortForward pfwd = client.pods().inNamespace(MY_NAMESPACE).withName(dbPod.getMetadata().getName()).portForward(5432);
            final String url = "jdbc:postgresql://localhost:" + pfwd.getLocalPort() + "/my_db";
            final Secret secret = client.secrets().inNamespace(MY_NAMESPACE)
                    .withName(NameUtils.standardAdminSecretName(entandoDatabaseService))
                    .get();
            final Connection connection = DriverManager
                    .getConnection(url, decode(secret, SecretUtils.USERNAME_KEY), decode(secret, SecretUtils.PASSSWORD_KEY));
            assertThat(connection.isValid(20)).isTrue();
        });
    }

    private String decode(Secret secret, String usernameKey) {
        return new String(Base64.decode(secret.getData().get(usernameKey)), StandardCharsets.UTF_8);
    }
}
