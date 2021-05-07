package org.entando.kubernetes.controller.databaseservice.inprocesstests;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.ServiceStatus;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.Watcher.Action;
import java.util.Collections;
import java.util.function.UnaryOperator;
import org.entando.kubernetes.controller.databaseservice.EntandoDatabaseServiceController;
import org.entando.kubernetes.controller.spi.common.EntandoOperatorSpiConfigProperty;
import org.entando.kubernetes.controller.support.client.SimpleK8SClient;
import org.entando.kubernetes.controller.support.client.SimpleKeycloakClient;
import org.entando.kubernetes.controller.support.client.doubles.EntandoResourceClientDouble;
import org.entando.kubernetes.controller.support.client.doubles.SimpleK8SClientDouble;
import org.entando.kubernetes.controller.support.command.InProcessCommandStream;
import org.entando.kubernetes.controller.support.common.KubeUtils;
import org.entando.kubernetes.model.common.DbmsVendor;
import org.entando.kubernetes.model.common.EntandoCustomResource;
import org.entando.kubernetes.model.externaldatabase.EntandoDatabaseService;
import org.entando.kubernetes.model.externaldatabase.EntandoDatabaseServiceBuilder;
import org.entando.kubernetes.model.externaldatabase.NestedEntandoDatabaseServiceFluent;
import org.entando.kubernetes.test.common.FluentTraversals;
import org.entando.kubernetes.test.componenttest.InProcessTestUtil;
import org.entando.kubernetes.test.componenttest.argumentcaptors.NamedArgumentCaptor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Spy;

@Tags({@Tag("post-deployment"), @Tag("pre-deployment"), @Tag("in-process"), @Tag("unit")})
//Because Sonar doesn't pick up custom captors
@SuppressWarnings("java:S6073")
class DeployDatabaseServiceTest implements InProcessTestUtil, FluentTraversals {

    public static final String MY_DATABASE_SERVICE = "my-database-service";
    @Spy
    private final SimpleK8SClient<EntandoResourceClientDouble> client = new SimpleK8SClientDouble();
    @Mock
    private SimpleKeycloakClient keycloakClient;
    private EntandoDatabaseServiceController databaseServiceController;

    @BeforeEach
    void createController() {
        databaseServiceController = new EntandoDatabaseServiceController(client.entandoResources(),
                new InProcessCommandStream(client, keycloakClient));
    }

    @Test
    void testServiceOnly() {
        EntandoDatabaseService database = createDatabaseService(builder -> builder.withCreateDeployment(false));
        emulateKeycloakDeployment(client);
        ServiceStatus serviceStatus = new ServiceStatus();
        lenient().when(client.services().loadService(argThat(matchesDatabaseService(database)), eq(MY_DATABASE_SERVICE + "-service")))
                .then(respondWithServiceStatus(serviceStatus));

        //When the the EntandoDatabaseServiceController is notified that a new EntandoDatabaseService has been added
        databaseServiceController.run();
        //Then a K8S Service was created with a name that reflects the EntandoDatabaseService and the fact that it is a DB service
        NamedArgumentCaptor<Service> serviceCaptor = forResourceNamed(Service.class, MY_DATABASE_SERVICE + "-db-service");
        verify(client.services()).createOrReplaceService(argThat(matchesDatabaseService(database)), serviceCaptor.capture());
        Service resultingService = serviceCaptor.getValue();
        //And the TCP port 3306 named 'db-port'
        ServicePort port = resultingService.getSpec().getPorts().get(0);
        assertThat(port.getPort(), is(5432));
        assertThat(resultingService.getSpec().getExternalName(), Matchers.is("somedatabase.com"));

    }

    private EntandoDatabaseService createDatabaseService(
            UnaryOperator<NestedEntandoDatabaseServiceFluent<EntandoDatabaseServiceBuilder>> modifier) {
        EntandoDatabaseService database = newTestEntandoDatabaseService(modifier);
        client.entandoResources().createOrPatchEntandoResource(database);
        System.setProperty(KubeUtils.ENTANDO_RESOURCE_ACTION, Action.ADDED.name());
        System.setProperty(EntandoOperatorSpiConfigProperty.ENTANDO_RESOURCE_NAMESPACE.getJvmSystemProperty(),
                database.getMetadata().getNamespace());
        System.setProperty(EntandoOperatorSpiConfigProperty.ENTANDO_RESOURCE_NAME.getJvmSystemProperty(), database.getMetadata().getName());
        System.setProperty(EntandoOperatorSpiConfigProperty.ENTANDO_RESOURCE_KIND.getJvmSystemProperty(), database.getKind());
        return database;
    }

    @Test
    void testServiceAndDeploymentWithGeneratedSecret() {
        EntandoDatabaseService database = createDatabaseService(builder -> builder.withCreateDeployment(true));
        emulateKeycloakDeployment(client);
        ServiceStatus serviceStatus = new ServiceStatus();
        lenient().when(client.services().loadService(argThat(matchesDatabaseService(database)), eq(MY_DATABASE_SERVICE + "-service")))
                .then(respondWithServiceStatus(serviceStatus));

        //When the the EntandoDatabaseServiceController is notified that a new EntandoDatabaseService has been added
        databaseServiceController.run();
        //Then a K8S Deployment was created with a name that reflects the EntandoDatabaseService and the fact that it is a DB service
        NamedArgumentCaptor<Deployment> deploymentCaptor = forResourceNamed(Deployment.class, MY_DATABASE_SERVICE + "-db-deployment");
        verify(client.deployments()).createOrPatchDeployment(argThat(matchesDatabaseService(database)), deploymentCaptor.capture());
        Deployment resultingDeployment = deploymentCaptor.getValue();
        //And the TCP port 3306 named 'db-port'
        assertThat(thePortNamed("db-port").on(thePrimaryContainerOn(resultingDeployment)).getContainerPort(), is(5432));
        verifyThatAllVolumesAreMapped(database, client, resultingDeployment);
        assertThat(theVariableReferenceNamed("POSTGRESQL_ADMIN_PASSWORD").on(thePrimaryContainerOn(resultingDeployment)).getSecretKeyRef()
                .getName(), is("my-database-service-db-admin-secret"));
        //Then a K8S Service was created with a name that reflects the EntandoDatabaseService and the fact that it is a DB service
        NamedArgumentCaptor<Service> serviceCaptor = forResourceNamed(Service.class, MY_DATABASE_SERVICE + "-db-service");
        verify(client.services()).createOrReplaceService(argThat(matchesDatabaseService(database)), serviceCaptor.capture());
        Service resultingService = serviceCaptor.getValue();
        //And the TCP port 3306 named 'db-port'
        ServicePort port = resultingService.getSpec().getPorts().get(0);
        assertThat(port.getPort(), is(5432));

    }

    @Test
    void testServiceAndDeploymentWithExistingSecret() {
        EntandoDatabaseService database = createDatabaseService(builder -> builder.withCreateDeployment(true).withSecretName("pg-secret"));
        ServiceStatus serviceStatus = new ServiceStatus();
        lenient().when(client.services().loadService(argThat(matchesDatabaseService(database)), eq(MY_DATABASE_SERVICE + "-service")))
                .then(respondWithServiceStatus(serviceStatus));

        //When the the EntandoDatabaseServiceController is notified that a new EntandoDatabaseService has been added
        databaseServiceController.run();
        //Then a K8S Deployment was created with a name that reflects the EntandoDatabaseService and the fact that it is a DB service
        NamedArgumentCaptor<Deployment> deploymentCaptor = forResourceNamed(Deployment.class, MY_DATABASE_SERVICE + "-db-deployment");
        verify(client.deployments())
                .createOrPatchDeployment(argThat(matchesDatabaseService(database)), deploymentCaptor.capture());

        Deployment resultingDeployment = deploymentCaptor.getValue();
        //And the TCP port 3306 named 'db-port'
        assertThat(thePortNamed("db-port").on(thePrimaryContainerOn(resultingDeployment)).getContainerPort(), is(5432));
        verifyThatAllVolumesAreMapped(database, client, resultingDeployment);
        assertThat(theVariableReferenceNamed("POSTGRESQL_ADMIN_PASSWORD").on(thePrimaryContainerOn(resultingDeployment)).getSecretKeyRef()
                .getName(), is("pg-secret"));
        //Then a K8S Service was created with a name that reflects the EntandoDatabaseService and the fact that it is a DB service
        NamedArgumentCaptor<Service> serviceCaptor = forResourceNamed(Service.class, MY_DATABASE_SERVICE + "-db-service");
        verify(client.services()).createOrReplaceService(argThat(matchesDatabaseService(database)), serviceCaptor.capture());
        Service resultingService = serviceCaptor.getValue();
        //And the TCP port 3306 named 'db-port'
        ServicePort port = resultingService.getSpec().getPorts().get(0);
        assertThat(port.getPort(), is(5432));

        //Then a K8S Deployment was created with a name that reflects the EntandoDatabaseService and the fact that it is a DB service
        NamedArgumentCaptor<PersistentVolumeClaim> pvcCaptor = forResourceNamed(PersistentVolumeClaim.class,
                MY_DATABASE_SERVICE + "-db-pvc");
        verify(client.persistentVolumeClaims()).createPersistentVolumeClaimIfAbsent(argThat(
                matchesDatabaseService(database)), pvcCaptor.capture());
    }

    private ArgumentMatcher<EntandoCustomResource> matchesDatabaseService(EntandoDatabaseService database) {
        return r -> r.getMetadata().equals(database.getMetadata());
    }

    @Test
    void testOverriddenPersistentVolumeClaimStorageClass() {
        //Given that I have configured the operator to use the clustered storage "azure-files"
        System.setProperty(EntandoOperatorSpiConfigProperty.ENTANDO_K8S_OPERATOR_DEFAULT_CLUSTERED_STORAGE_CLASS.getJvmSystemProperty(),
                "azure-files");
        //And I have configured the operator to use the non clustered storage "some-persistence-provider"
        System.setProperty(EntandoOperatorSpiConfigProperty.ENTANDO_K8S_OPERATOR_DEFAULT_NON_CLUSTERED_STORAGE_CLASS.getJvmSystemProperty(),
                "some-persistence-provider");
        //Given that I have configured the operator to use required ReadWriteMany access mode
        System.setProperty(EntandoOperatorSpiConfigProperty.ENTANDO_K8S_OPERATOR_PVC_ACCESSMODE_OVERRIDE.getJvmSystemProperty(),
                "ReadWriteMany");
        EntandoDatabaseService database = createDatabaseService(
                builder -> builder.withCreateDeployment(true).withStorageClass("azure-disk"));
        emulateKeycloakDeployment(client);
        //When the the EntandoDatabaseServiceController is notified that a new EntandoDatabaseService has been added
        databaseServiceController.run();
        //Then the PersistentVolumeClaim attributes are suitable for a single pod container
        NamedArgumentCaptor<PersistentVolumeClaim> pvcCaptor = forResourceNamed(PersistentVolumeClaim.class,
                MY_DATABASE_SERVICE + "-db-pvc");
        verify(client.persistentVolumeClaims()).createPersistentVolumeClaimIfAbsent(argThat(matchesDatabaseService(database)), pvcCaptor.capture());
        assertThat(pvcCaptor.getValue().getSpec().getAccessModes(), is(Collections.singletonList("ReadWriteOnce")));
        //And the storageClass "azure-disk" as specified in the EntandoDatabaseService is used.
        assertThat(pvcCaptor.getValue().getSpec().getStorageClassName(), is("azure-disk"));
    }

    @BeforeEach
    @AfterEach
    void resetSystemProperties() {
        System.clearProperty(EntandoOperatorSpiConfigProperty.ENTANDO_K8S_OPERATOR_DEFAULT_CLUSTERED_STORAGE_CLASS.getJvmSystemProperty());
        System.clearProperty(
                EntandoOperatorSpiConfigProperty.ENTANDO_K8S_OPERATOR_DEFAULT_NON_CLUSTERED_STORAGE_CLASS.getJvmSystemProperty());
        System.clearProperty(EntandoOperatorSpiConfigProperty.ENTANDO_K8S_OPERATOR_PVC_ACCESSMODE_OVERRIDE.getJvmSystemProperty());

    }

    @Test
    void testDefaultPersistentVolumeClaimStorageClass() {
        //Given that I have configured the operator to use the clustered storage "azure-files"
        System.setProperty(EntandoOperatorSpiConfigProperty.ENTANDO_K8S_OPERATOR_DEFAULT_CLUSTERED_STORAGE_CLASS.getJvmSystemProperty(),
                "azure-files");
        //And I have configured the operator to use the non clustered storage "azure-disk"
        System.setProperty(EntandoOperatorSpiConfigProperty.ENTANDO_K8S_OPERATOR_DEFAULT_NON_CLUSTERED_STORAGE_CLASS.getJvmSystemProperty(),
                "azure-disk");
        //Given that I have configured the operator to use required ReadWriteMany access mode
        System.setProperty(EntandoOperatorSpiConfigProperty.ENTANDO_K8S_OPERATOR_PVC_ACCESSMODE_OVERRIDE.getJvmSystemProperty(),
                "ReadWriteMany");
        EntandoDatabaseService database = createDatabaseService(
                builder -> builder.withCreateDeployment(true));
        emulateKeycloakDeployment(client);
        //When the EntandoDatabaseServiceController is notified that a new EntandoDatabaseService has been added
        databaseServiceController.run();
        //Then the PersistentVolumeClaim attributes are suitable for a single pod container
        NamedArgumentCaptor<PersistentVolumeClaim> pvcCaptor = forResourceNamed(PersistentVolumeClaim.class,
                MY_DATABASE_SERVICE + "-db-pvc");
        verify(client.persistentVolumeClaims()).createPersistentVolumeClaimIfAbsent(argThat(matchesDatabaseService(database)), pvcCaptor.capture());
        //And the ReadWriteMany access mode is overridden for the single-pod database container
        assertThat(pvcCaptor.getValue().getSpec().getAccessModes(), is(Collections.singletonList("ReadWriteOnce")));
        //And the non-clustered storageClass "azure-disk" is used
        assertThat(pvcCaptor.getValue().getSpec().getStorageClassName(), is("azure-disk"));
    }

    private EntandoDatabaseService newTestEntandoDatabaseService(
            UnaryOperator<NestedEntandoDatabaseServiceFluent<EntandoDatabaseServiceBuilder>> modifier) {
        return modifier.apply(new EntandoDatabaseServiceBuilder()
                .withNewMetadata().withName(MY_DATABASE_SERVICE).withNamespace("my-namespace").endMetadata()
                .withNewSpec()
                .withDbms(DbmsVendor.POSTGRESQL)
                .withHost("somedatabase.com")).endSpec()
                .build();
    }
}
