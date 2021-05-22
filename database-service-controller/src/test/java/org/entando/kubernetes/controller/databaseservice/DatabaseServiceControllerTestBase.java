package org.entando.kubernetes.controller.databaseservice;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.entando.kubernetes.controller.spi.capability.CapabilityProvider;
import org.entando.kubernetes.controller.spi.capability.SerializingCapabilityProvider;
import org.entando.kubernetes.controller.spi.command.DeploymentProcessor;
import org.entando.kubernetes.controller.spi.common.EntandoOperatorSpiConfigProperty;
import org.entando.kubernetes.controller.support.client.doubles.SimpleK8SClientDouble;
import org.entando.kubernetes.controller.support.common.EntandoOperatorConfigProperty;
import org.entando.kubernetes.test.common.ControllerTestHelper;
import org.entando.kubernetes.test.common.FluentTraversals;
import org.junit.jupiter.api.AfterEach;

class DatabaseServiceControllerTestBase implements FluentTraversals, ControllerTestHelper {

    static final String DEFAULT_DBMS_IN_NAMESPACE = "default-dbms-in-namespace";

    protected final SimpleK8SClientDouble client = new SimpleK8SClientDouble();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

    @Override
    public SimpleK8SClientDouble getClient() {
        return client;
    }

    public CapabilityProvider getCapabilityProvider() {
        return new SerializingCapabilityProvider(client.entandoResources(), new AllureAttachingCommandStream(client, null));
    }

    @Override
    public ScheduledExecutorService getScheduler() {
        return scheduledExecutorService;
    }

    @Override
    public Runnable createController(DeploymentProcessor deploymentProcessor) {
        return new EntandoDatabaseServiceController(client.entandoResources(), deploymentProcessor);
    }

    @AfterEach
    void resetSystemProps() {
        System.getProperties().remove(EntandoOperatorSpiConfigProperty.ENTANDO_K8S_OPERATOR_COMPLIANCE_MODE.getJvmSystemProperty());
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

}
