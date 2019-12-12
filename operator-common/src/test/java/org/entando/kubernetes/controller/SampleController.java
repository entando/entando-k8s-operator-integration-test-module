package org.entando.kubernetes.controller;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.quarkus.runtime.StartupEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import javax.enterprise.event.Observes;
import org.entando.kubernetes.controller.database.DatabaseServiceResult;
import org.entando.kubernetes.controller.k8sclient.SimpleK8SClient;
import org.entando.kubernetes.controller.spi.Deployable;
import org.entando.kubernetes.model.EntandoDeploymentSpec;
import org.entando.kubernetes.model.app.EntandoBaseCustomResource;

public class SampleController<T extends EntandoBaseCustomResource> extends AbstractDbAwareController<T> {

    public SampleController(KubernetesClient kubernetesClient) {
        super(kubernetesClient, false);
    }

    public SampleController(SimpleK8SClient<?> k8sClient, SimpleKeycloakClient keycloakClient) {
        super(k8sClient, keycloakClient);
    }

    public void onStartup(@Observes StartupEvent event) {
        processCommand();
    }

    protected void processAddition(T newKeycloakServer) {
        // Create database for Keycloak
        EntandoDeploymentSpec spec = resolveSpec(newKeycloakServer);
        DatabaseServiceResult databaseServiceResult = prepareDatabaseService(newKeycloakServer, spec.getDbms(),
                "db");
        // Create the Keycloak service using the provided database
        KeycloakConnectionConfig keycloakConnectionConfig = k8sClient.entandoResources().findKeycloak(() -> Optional.empty());
        Deployable<ServiceDeploymentResult> keycloakDeployable = createDeployable(newKeycloakServer, databaseServiceResult,
                keycloakConnectionConfig);
        DeployCommand<ServiceDeploymentResult> keycloakCommand = new DeployCommand<>(keycloakDeployable);
        ServiceDeploymentResult keycloakDeploymentResult = keycloakCommand.execute(k8sClient, Optional.of(keycloakClient));
        k8sClient.entandoResources().updateStatus(newKeycloakServer, keycloakCommand.getStatus());
    }

    private EntandoDeploymentSpec resolveSpec(T r) {
        Object spec = null;
        try {
            spec = r.getClass().getMethod("getSpec").invoke(r);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
        return (EntandoDeploymentSpec) spec;
    }

    protected Deployable<ServiceDeploymentResult> createDeployable(T newKeycloakServer,
            DatabaseServiceResult databaseServiceResult,
            KeycloakConnectionConfig keycloakConnectionConfig) {
        return new SampleServerDeployable<>(newKeycloakServer, databaseServiceResult, keycloakConnectionConfig);
    }

}
