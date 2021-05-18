package org.entando.kubernetes.controller.databaseservice.inprocesstests;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import java.util.List;
import org.entando.kubernetes.controller.databaseservice.EntandoDatabaseServiceController;
import org.entando.kubernetes.controller.spi.client.KubernetesClientForControllers;
import org.entando.kubernetes.controller.spi.command.CommandStream;
import org.entando.kubernetes.controller.spi.command.DefaultSerializableDeploymentResult;
import org.entando.kubernetes.controller.spi.command.DeserializationHelper;
import org.entando.kubernetes.controller.spi.command.SerializableDeploymentResult;
import org.entando.kubernetes.controller.spi.command.SerializationHelper;
import org.entando.kubernetes.controller.spi.container.ConfigurableResourceContainer;
import org.entando.kubernetes.controller.spi.container.DeployableContainer;
import org.entando.kubernetes.controller.spi.container.HasHealthCommand;
import org.entando.kubernetes.controller.spi.container.PersistentVolumeAware;
import org.entando.kubernetes.controller.spi.container.ServiceBackingContainer;
import org.entando.kubernetes.controller.spi.deployable.Deployable;
import org.entando.kubernetes.model.capability.CapabilityProvisioningStrategy;
import org.entando.kubernetes.model.capability.CapabilityScope;
import org.entando.kubernetes.model.capability.ProvidedCapability;
import org.entando.kubernetes.model.capability.ProvidedCapabilityBuilder;
import org.entando.kubernetes.model.capability.StandardCapability;
import org.entando.kubernetes.model.capability.StandardCapabilityImplementation;
import org.entando.kubernetes.model.common.AbstractServerStatus;
import org.entando.kubernetes.model.common.DbmsVendor;
import org.entando.kubernetes.model.common.InternalServerStatus;
import org.entando.kubernetes.model.externaldatabase.EntandoDatabaseService;
import org.entando.kubernetes.test.componenttest.InProcessTestUtil;
import org.entando.kubernetes.test.componenttest.argumentcaptors.NamedArgumentCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * This is my first attempt at an in-process component scoped test for the new Controller architecture. Component Scope: the
 * entando-k8s-database-service-controller Functional Scope: the PostgreSQL implementation Boundary classes: CommandStream facilitates
 * communication with the DeployCommand in entando-k8s-operator-common. The outgoing contract here is the JSON string representing the
 * Deployable and DeployableContainers and the incoming contract is the SerializableDeploymentResult CustomResourceClient facilitates direct
 * communication with Kubernetes itself. What to assert: 1. That the outgoing Deployable and DeployableContainers are correct 2. That the
 * appropriate custom resource was created (EntandoDatabaseService from ProvidedCapability, or ProvidedCapability from
 * EntandoDatabaseService) 3. That both custom resources received status updates. 4. That all status updates on these resources used the
 * information provided from the incoming SerializableDeploymentResult
 */
@Tags({@Tag("in-process"), @Tag("component")})
class PostgreSqlTest implements InProcessTestUtil {

    private EntandoDatabaseServiceController databaseServiceController;
    @Mock
    private CommandStream commandStream;
    @Mock
    private KubernetesClientForControllers customResourceClient;

    @BeforeEach
    void createController() {
        MockitoAnnotations.openMocks(this);
        databaseServiceController = new EntandoDatabaseServiceController(customResourceClient, commandStream);
    }

    @Test
    void shouldCreateAndProcessEntandoDatabaseServiceFromProvidedCapability() {
        //Given I have a ProvidedCapability asking for a PostgreSQL database service to be deployed directly at namespace scope
        ProvidedCapability providedCapability = new ProvidedCapabilityBuilder()
                .withNewMetadata()
                .withNamespace("my-namespace")
                .withName("my-postgresql")
                .endMetadata()
                .withNewSpec()
                .withCapability(StandardCapability.DBMS)
                .withImplementation(StandardCapabilityImplementation.POSTGRESQL)
                .withCapabilityRequirementScope(CapabilityScope.NAMESPACE)
                .withProvisioningStrategy(CapabilityProvisioningStrategy.DEPLOY_DIRECTLY)
                .endSpec()
                .build();
        Mockito.when(customResourceClient.resolveCustomResourceToProcess(any())).thenReturn(providedCapability);
        //And I can create EntandoDatbaseService custom resources on Kubernetes
        Mockito.when(
                customResourceClient.createOrPatchEntandoResource(argThat(matchesName(EntandoDatabaseService.class, "my-postgresql"))))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        //And I can send requests for a Deployable to be process by the entando-k8s-operator-common command infrastructure
        final SerializableDeploymentResult<?> deploymentResult = buildDbServerStatus();
        Mockito.when(commandStream.process(any())).thenReturn(SerializationHelper.serialize(deploymentResult));

        //When the DatabaseServiceController is run
        databaseServiceController.run();

        //Then a new EntandoDatabaseService was created for the  requested capability
        NamedArgumentCaptor<EntandoDatabaseService> databaseServiceCaptor = forResourceNamed(EntandoDatabaseService.class, "my-postgresql");
        verify(customResourceClient, times(1)).createOrPatchEntandoResource(databaseServiceCaptor.capture());
        //And this EntandoDatabaseService reflects the state of the requested Capability
        final EntandoDatabaseService actualDatabasedServiceCreated = databaseServiceCaptor.getValue();
        assertThat(actualDatabasedServiceCreated.getSpec().getCreateDeployment().get(), is(true));
        assertThat(actualDatabasedServiceCreated.getSpec().getDbms(), is(DbmsVendor.POSTGRESQL));
        assertTrue(actualDatabasedServiceCreated.getSpec().getDatabaseName().isEmpty());
        assertTrue(actualDatabasedServiceCreated.getSpec().getHost().isEmpty());
        assertTrue(actualDatabasedServiceCreated.getSpec().getPort().isEmpty());
        assertTrue(actualDatabasedServiceCreated.getSpec().getJdbcParameters().isEmpty());
        //Then exactly one Deployable instance was sent through the CommandStream
        ArgumentCaptor<String> deployable = ArgumentCaptor.forClass(String.class);
        Mockito.verify(commandStream, times(1)).process(deployable.capture());
        Deployable<?> databaseServiceDeployable = DeserializationHelper
                .deserialize(customResourceClient, deployable.getValue());
        //And no ExternalService to connect to was specified
        assertThat(databaseServiceDeployable.getExternalService().isPresent(), is(false));
        //And exactly one container was specified
        assertThat(databaseServiceDeployable.getContainers().size(), is(1));
        final DeployableContainer dbContainer = databaseServiceDeployable.getContainers().get(0);
        //Using the correct PostgreSQL image
        assertThat(dbContainer.getDockerImageInfo().getRepository(), is("postgresql-12-centos7"));
        final List<EnvVar> vars = dbContainer.getEnvironmentVariables();
        //With all the necessary Environment Variables to successfully initialize the PostgreSQL container
        assertEnvironmentVariable(vars, "POSTGRESQL_DATABASE", "my_postgresql_db");
        assertEnvironmentVariable(vars, "POSTGRESQL_USER", "my_postgresql_db_user");
        assertEnvironmentVariableReference(vars, "POSTGRESQL_PASSWORD", "my-postgresql-admin-secret", "password");
        assertEnvironmentVariableReference(vars, "POSTGRESQL_ADMIN_PASSWORD", "my-postgresql-admin-secret", "password");
        //And the container specifies a  PersistentValueClaim mounted in the standard PostgreSQL data directory
        assertThat(dbContainer, instanceOf(PersistentVolumeAware.class));
        assertThat(((PersistentVolumeAware) dbContainer).getVolumeMountPath(), is("/var/lib/pgsql/data"));
        assertThat(dbContainer, instanceOf(ServiceBackingContainer.class));
        //And no additional resources are requested
        assertThat(dbContainer, instanceOf(ConfigurableResourceContainer.class));
        assertThat(((ConfigurableResourceContainer) dbContainer).getResourceRequirementsOverride().isPresent(), is(false));
        //And the health command reflects the built-in check-container health check that comes with the container
        assertThat(dbContainer, instanceOf(HasHealthCommand.class));
        assertThat(((HasHealthCommand) dbContainer).getHealthCheckCommand(), is("/usr/libexec/check-container"));
        //And a status update was sent to the EntandoDatabaseService;
        ArgumentCaptor<AbstractServerStatus> statusCaptor = ArgumentCaptor.forClass(AbstractServerStatus.class);
        verify(this.customResourceClient, times(1)).updateStatus(databaseServiceCaptor.capture(), statusCaptor.capture());
        assertThat(databaseServiceCaptor.getValue(), sameInstance(actualDatabasedServiceCreated));
        NamedArgumentCaptor<ProvidedCapability> capabilityCaptor = forResourceNamed(ProvidedCapability.class, "my-postgresql");
        verify(this.customResourceClient, times(1)).updateStatus(capabilityCaptor.capture(), statusCaptor.capture());
        assertThat(capabilityCaptor.getValue(), sameInstance(providedCapability));
    }

    private <T> ArgumentMatcher<T> matchesName(Class<T> clazz, String name) {
        return argument -> clazz.isInstance(argument) && ((HasMetadata) argument).getMetadata().getName().equals(name);
    }

    void assertEnvironmentVariableReference(List<EnvVar> vars, String name, String secretName, String secretKey) {
        assertThat(
                vars.stream().filter(envVar -> envVar.getName().equals(name)).findFirst().get().getValueFrom().getSecretKeyRef().getName(),
                is(secretName));
        assertThat(
                vars.stream().filter(envVar -> envVar.getName().equals(name)).findFirst().get().getValueFrom().getSecretKeyRef().getKey(),
                is(secretKey));
    }

    void assertEnvironmentVariable(List<EnvVar> vars, String name, String value) {
        assertThat(vars.stream().filter(envVar -> envVar.getName().equals(name)).findFirst().get().getValue(),
                is(value));
    }

    private SerializableDeploymentResult<?> buildDbServerStatus() {
        final DefaultSerializableDeploymentResult result = new DefaultSerializableDeploymentResult(null, null, new ServiceBuilder()
                .withNewMetadata()
                .withName("my-postgresql-service")
                .endMetadata()
                .withNewSpec()
                .addNewPort()
                .withPort(5432)
                .endPort()
                .endSpec()
                .build(), null);
        result.withStatus(new InternalServerStatus());
        return result;
    }

}
