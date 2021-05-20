package org.entando.kubernetes.controller.databaseservice;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.entando.kubernetes.controller.spi.command.CommandStream;
import org.entando.kubernetes.controller.spi.common.EntandoOperatorSpiConfigProperty;
import org.entando.kubernetes.controller.support.client.SimpleK8SClient;
import org.entando.kubernetes.controller.support.client.SimpleKeycloakClient;
import org.entando.kubernetes.controller.support.client.doubles.EntandoResourceClientDouble;
import org.entando.kubernetes.controller.support.client.doubles.SimpleK8SClientDouble;
import org.entando.kubernetes.controller.support.common.EntandoOperatorConfigProperty;
import org.entando.kubernetes.test.common.ControllerTestHelper;
import org.entando.kubernetes.test.common.FluentTraversals;
import org.junit.jupiter.api.AfterEach;
import org.mockito.Spy;

class DatabaseServiceControllerTestBase implements FluentTraversals, ControllerTestHelper {

    static final String DEFAULT_DBMS_IN_NAMESPACE = "default-dbms-in-namespace";

    @Spy
    protected final SimpleK8SClient<EntandoResourceClientDouble> client = new SimpleK8SClientDouble();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

    @Override
    public SimpleK8SClient<EntandoResourceClientDouble> getClient() {
        return client;
    }

    @Override
    public ScheduledExecutorService getScheduler() {
        return scheduledExecutorService;
    }

    @AfterEach
    void resetSystemProps() {
        System.getProperties().remove(EntandoOperatorConfigProperty.ENTANDO_K8S_OPERATOR_COMPLIANCE_MODE.getJvmSystemProperty());
        System.getProperties().remove(EntandoOperatorSpiConfigProperty.ENTANDO_CA_SECRET_NAME.getJvmSystemProperty());
        System.getProperties().remove(EntandoOperatorConfigProperty.ENTANDO_TLS_SECRET_NAME.getJvmSystemProperty());
        System.getProperties().remove(EntandoOperatorConfigProperty.ENTANDO_DOCKER_IMAGE_VERSION_FALLBACK.getJvmSystemProperty());
        System.getProperties().remove(EntandoOperatorConfigProperty.ENTANDO_REQUIRES_FILESYSTEM_GROUP_OVERRIDE.getJvmSystemProperty());
        System.getProperties().remove(EntandoOperatorConfigProperty.ENTANDO_INGRESS_CLASS.getJvmSystemProperty());
        System.getProperties()
                .remove(EntandoOperatorSpiConfigProperty.ENTANDO_K8S_OPERATOR_DEFAULT_CLUSTERED_STORAGE_CLASS.getJvmSystemProperty());
        System.getProperties()
                .remove(EntandoOperatorSpiConfigProperty.ENTANDO_K8S_OPERATOR_DEFAULT_NON_CLUSTERED_STORAGE_CLASS.getJvmSystemProperty());
    }

    @Override
    public Runnable createController(SimpleK8SClient<EntandoResourceClientDouble> client, SimpleKeycloakClient keycloakClient,
            CommandStream commandStream) {
        return new EntandoDatabaseServiceController(client.entandoResources(), commandStream);
    }

}
