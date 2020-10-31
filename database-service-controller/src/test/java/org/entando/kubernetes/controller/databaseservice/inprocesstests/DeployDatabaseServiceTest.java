package org.entando.kubernetes.controller.databaseservice.inprocesstests;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.ServiceStatus;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.Watcher.Action;
import io.quarkus.runtime.StartupEvent;
import org.entando.kubernetes.controller.KubeUtils;
import org.entando.kubernetes.controller.SimpleKeycloakClient;
import org.entando.kubernetes.controller.databaseservice.EntandoDatabaseServiceController;
import org.entando.kubernetes.controller.inprocesstest.InProcessTestUtil;
import org.entando.kubernetes.controller.inprocesstest.argumentcaptors.NamedArgumentCaptor;
import org.entando.kubernetes.controller.inprocesstest.k8sclientdouble.EntandoResourceClientDouble;
import org.entando.kubernetes.controller.inprocesstest.k8sclientdouble.SimpleK8SClientDouble;
import org.entando.kubernetes.controller.k8sclient.SimpleK8SClient;
import org.entando.kubernetes.controller.test.support.FluentTraversals;
import org.entando.kubernetes.model.DbmsVendor;
import org.entando.kubernetes.model.externaldatabase.EntandoDatabaseService;
import org.entando.kubernetes.model.externaldatabase.EntandoDatabaseServiceBuilder;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Spy;

@Tags({@Tag("post-deployment"), @Tag("pre-deployment"), @Tag("in-process"), @Tag("unit")})
public class DeployDatabaseServiceTest implements InProcessTestUtil, FluentTraversals {

    public static final String MY_DATABASE_SERVICE = "my-database-service";
    @Spy
    private final SimpleK8SClient<EntandoResourceClientDouble> client = new SimpleK8SClientDouble();
    @Mock
    private SimpleKeycloakClient keycloakClient;
    private EntandoDatabaseServiceController databaseServiceController;

    @BeforeEach
    void createReusedSecrets() {
        client.secrets().overwriteControllerSecret(buildInfrastructureSecret());
        emulateKeycloakDeployment(client);
        databaseServiceController = new EntandoDatabaseServiceController(client, keycloakClient);
    }

    @Test
    public void testServiceOnly() {
        EntandoDatabaseService database = createDatabaseService(false);
        ServiceStatus serviceStatus = new ServiceStatus();
        lenient().when(client.services().loadService(eq(database), eq(MY_DATABASE_SERVICE + "-service")))
                .then(respondWithServiceStatus(serviceStatus));

        //When the the EntandoDatabaseServiceController is notified that a new EntandoDatabaseService has been added
        databaseServiceController.onStartup(new StartupEvent());
        //Then a K8S Service was created with a name that reflects the EntandoDatabaseService and the fact that it is a DB service
        NamedArgumentCaptor<Service> serviceCaptor = forResourceNamed(Service.class, MY_DATABASE_SERVICE + "-db-service");
        verify(client.services()).createOrReplaceService(eq(database), serviceCaptor.capture());
        Service resultingService = serviceCaptor.getValue();
        //And the TCP port 3306 named 'db-port'
        ServicePort port = resultingService.getSpec().getPorts().get(0);
        assertThat(port.getPort(), is(5432));
        assertThat(resultingService.getSpec().getExternalName(), Matchers.is("somedatabase.com"));

    }

    private EntandoDatabaseService createDatabaseService(boolean createDeployment) {
        EntandoDatabaseService database = newTestEntandoDatabaseService(createDeployment);
        client.entandoResources().createOrPatchEntandoResource(database);
        System.setProperty(KubeUtils.ENTANDO_RESOURCE_ACTION, Action.ADDED.name());
        System.setProperty(KubeUtils.ENTANDO_RESOURCE_NAMESPACE, database.getMetadata().getNamespace());
        System.setProperty(KubeUtils.ENTANDO_RESOURCE_NAME, database.getMetadata().getName());
        return database;
    }

    @Test
    public void testServiceAndDeployment() {
        EntandoDatabaseService database = createDatabaseService(true);
        ServiceStatus serviceStatus = new ServiceStatus();
        lenient().when(client.services().loadService(eq(database), eq(MY_DATABASE_SERVICE + "-service")))
                .then(respondWithServiceStatus(serviceStatus));

        //When the the EntandoDatabaseServiceController is notified that a new EntandoDatabaseService has been added
        databaseServiceController.onStartup(new StartupEvent());
        //Then a K8S Deployment was created with a name that reflects the EntandoDatabaseService and the fact that it is a DB service
        NamedArgumentCaptor<Deployment> deploymentCaptor = forResourceNamed(Deployment.class, MY_DATABASE_SERVICE + "-db-deployment");
        verify(client.deployments()).createOrPatchDeployment(eq(database), deploymentCaptor.capture());

        Deployment resultingDeployment = deploymentCaptor.getValue();
        //And the TCP port 3306 named 'db-port'
        assertThat(thePortNamed("db-port").on(thePrimaryContainerOn(resultingDeployment)).getContainerPort(), is(5432));
        //Then a K8S Service was created with a name that reflects the EntandoDatabaseService and the fact that it is a DB service
        NamedArgumentCaptor<Service> serviceCaptor = forResourceNamed(Service.class, MY_DATABASE_SERVICE + "-db-service");
        verify(client.services()).createOrReplaceService(eq(database), serviceCaptor.capture());
        Service resultingService = serviceCaptor.getValue();
        //And the TCP port 3306 named 'db-port'
        ServicePort port = resultingService.getSpec().getPorts().get(0);
        assertThat(port.getPort(), is(5432));

    }

    private EntandoDatabaseService newTestEntandoDatabaseService(boolean createDeployment) {
        return new EntandoDatabaseServiceBuilder()
                .withNewMetadata().withName(MY_DATABASE_SERVICE).withNamespace("my-namespace").endMetadata()
                .withNewSpec()
                .withDbms(DbmsVendor.POSTGRESQL)
                .withHost("somedatabase.com")
                .withCreateDeployment(createDeployment)
                .withSecretName("oracle-secret")
                .endSpec()
                .build();
    }
}
