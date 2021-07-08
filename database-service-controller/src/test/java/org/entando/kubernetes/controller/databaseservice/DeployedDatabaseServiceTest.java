package org.entando.kubernetes.controller.databaseservice;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import java.util.Map;
import java.util.stream.Stream;
import org.entando.kubernetes.controller.spi.command.SerializationHelper;
import org.entando.kubernetes.controller.spi.common.DbmsDockerVendorStrategy;
import org.entando.kubernetes.controller.spi.common.DbmsVendorConfig;
import org.entando.kubernetes.controller.spi.common.EntandoOperatorComplianceMode;
import org.entando.kubernetes.controller.spi.common.EntandoOperatorSpiConfigProperty;
import org.entando.kubernetes.controller.spi.common.LabelNames;
import org.entando.kubernetes.controller.spi.common.NameUtils;
import org.entando.kubernetes.controller.spi.common.ResourceUtils;
import org.entando.kubernetes.controller.spi.common.SecretUtils;
import org.entando.kubernetes.controller.spi.container.ProvidedDatabaseCapability;
import org.entando.kubernetes.controller.spi.result.DatabaseConnectionInfo;
import org.entando.kubernetes.controller.support.common.EntandoOperatorConfigProperty;
import org.entando.kubernetes.model.capability.CapabilityProvisioningStrategy;
import org.entando.kubernetes.model.capability.CapabilityScope;
import org.entando.kubernetes.model.capability.ProvidedCapability;
import org.entando.kubernetes.model.capability.StandardCapabilityImplementation;
import org.entando.kubernetes.model.common.DbmsVendor;
import org.entando.kubernetes.model.externaldatabase.EntandoDatabaseService;
import org.entando.kubernetes.model.externaldatabase.EntandoDatabaseServiceBuilder;
import org.entando.kubernetes.test.common.SourceLink;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Tags({@Tag("component"), @Tag("in-process"), @Tag("inner-hexagon")})
@Feature("As a deployer, I would like to deploy an EntandoDatabaseService directly so that I have more granular control over the "
        + "configuration settings")
@Issue("ENG-2284")
@SourceLink("DeployedDatabaseServiceTest.java")
class DeployedDatabaseServiceTest extends DatabaseServiceControllerTestBase {

    private static Stream<Arguments> providePostgresqlParameters() {
        return Stream.of(
                Arguments.of(EntandoOperatorComplianceMode.REDHAT, DbmsDockerVendorStrategy.RHEL_POSTGRESQL),
                Arguments.of(EntandoOperatorComplianceMode.COMMUNITY, DbmsDockerVendorStrategy.CENTOS_POSTGRESQL)
        );
    }

    private static Stream<Arguments> provideMysqlParameters() {
        return Stream.of(
                Arguments.of(EntandoOperatorComplianceMode.REDHAT, DbmsDockerVendorStrategy.RHEL_MYSQL),
                Arguments.of(EntandoOperatorComplianceMode.COMMUNITY, DbmsDockerVendorStrategy.CENTOS_MYSQL)
        );
    }

    @ParameterizedTest(name = ParameterizedTest.DISPLAY_NAME_PLACEHOLDER + ": [" + ParameterizedTest.ARGUMENTS_WITH_NAMES_PLACEHOLDER + "]")
    @MethodSource("providePostgresqlParameters")
    @DisplayName("Should deploy the correct PostgreSQL image with default configuration settings for the currently selected compliance "
            + "mode")
    @Description("Should deploy the correct PostgreSQL image with default configuration settings for the currently selected compliance "
            + "mode")
    void shouldDeployCorrectPostgresqlImageWithDefaultSettings(EntandoOperatorComplianceMode complianceMode,
            DbmsDockerVendorStrategy expectedDbmsStrategy) {
        step("Given that the Entando Operator is running in '" + complianceMode.getName() + "' compliance mode ",
                () -> attachEnvironmentVariable(EntandoOperatorSpiConfigProperty.ENTANDO_K8S_OPERATOR_COMPLIANCE_MODE,
                        complianceMode.getName()));
        step("And the Operator runs in a Kubernetes environment the requires a filesystem user/group override for mounted volumes",
                () -> attachEnvironmentVariable(EntandoOperatorConfigProperty.ENTANDO_REQUIRES_FILESYSTEM_GROUP_OVERRIDE, "true"));

        step("When I create an EntandoDatabaseService with no additional parameters",
                () -> runControllerAgainstCustomResource(new EntandoDatabaseServiceBuilder()
                        .withNewMetadata()
                        .withName(DEFAULT_DBMS_IN_NAMESPACE)
                        .withNamespace(MY_NAMESPACE)
                        .endMetadata()
                        .withNewSpec()
                        .withCreateDeployment(true)
                        .withProvidedCapabilityScope(CapabilityScope.NAMESPACE)
                        .endSpec()
                        .build()));
        final ProvidedCapability providedCapability = client.entandoResources()
                .load(ProvidedCapability.class, MY_NAMESPACE, DEFAULT_DBMS_IN_NAMESPACE);
        final EntandoDatabaseService entandoDatabaseService = client.entandoResources()
                .load(EntandoDatabaseService.class, MY_NAMESPACE, DEFAULT_DBMS_IN_NAMESPACE);
        step("Then a ProvidedCapability was made available:", () -> {
            step("using the DeployDirectly provisioningStrategy",
                    () -> assertThat(providedCapability.getSpec().getResolutionScopePreference()).contains(CapabilityScope.NAMESPACE));
            step("a PostgreSQL database",
                    () -> assertThat(providedCapability.getSpec().getImplementation())
                            .contains(StandardCapabilityImplementation.POSTGRESQL));
            step("and it is owned by the ProvidedCapability to ensure only changes from the ProvidedCapability will change the "
                            + "implementing Kubernetes resources",
                    () -> assertThat(ResourceUtils.customResourceOwns(entandoDatabaseService, providedCapability)));
            attachKubernetesResource("ProvidedCapability", providedCapability);
        });
        step("And a Kubernetes Deployment was created reflecting the requirements of the PostgreSQL image:" + expectedDbmsStrategy
                        .getOrganization() + "/" + expectedDbmsStrategy.getImageRepository(),
                () -> {
                    final Deployment deployment = client.deployments()
                            .loadDeployment(entandoDatabaseService, NameUtils.standardDeployment(entandoDatabaseService));
                    attachKubernetesResource("Deployment", deployment);
                    step("using the PostgreSQL Image " + expectedDbmsStrategy.getOrganization() + "/" + expectedDbmsStrategy
                                    .getImageRepository(),
                            () -> assertThat(thePrimaryContainerOn(deployment).getImage())
                                    .contains(expectedDbmsStrategy.getOrganization() + "/" + expectedDbmsStrategy.getImageRepository()));
                    step("With a volume mounted to the standard data directory /var/lib/pgsql/data",
                            () -> assertThat(
                                    theVolumeMountNamed("default-dbms-in-namespace-db-volume").on(thePrimaryContainerOn(deployment))
                                            .getMountPath()).isEqualTo("/var/lib/pgsql/data"));
                    step("Which is bound to a PersistentVolumeClain", () -> {
                        final PersistentVolumeClaim pvc = client.persistentVolumeClaims()
                                .loadPersistentVolumeClaim(entandoDatabaseService, "default-dbms-in-namespace-db-pvc");
                        attachKubernetesResource("PersistentVolumeClaim", pvc);
                        assertThat(theVolumeNamed("default-dbms-in-namespace-db-volume").on(deployment).getPersistentVolumeClaim()
                                .getClaimName()).isEqualTo(
                                "default-dbms-in-namespace-db-pvc");
                    });
                    step("And livenessProbe, startupProbe and readinessProbe all use the standard PostgreSQL command "
                                    + "/usr/libexec/check-container",
                            () -> {
                                assertThat(thePrimaryContainerOn(deployment).getLivenessProbe().getExec().getCommand())
                                        .contains("/usr/libexec/check-container");
                                assertThat(thePrimaryContainerOn(deployment).getReadinessProbe().getExec().getCommand())
                                        .contains("/usr/libexec/check-container");
                                assertThat(thePrimaryContainerOn(deployment).getStartupProbe().getExec().getCommand())
                                        .contains("/usr/libexec/check-container");
                            });

                    step("And the File System User/Group override " + DbmsDockerVendorStrategy.RHEL_POSTGRESQL.getFileSystemUserGroupid()
                            .get()
                            + "has been applied to the mount", () ->
                            assertThat(deployment.getSpec().getTemplate().getSpec().getSecurityContext().getFsGroup())
                                    .isEqualTo(DbmsDockerVendorStrategy.RHEL_POSTGRESQL.getFileSystemUserGroupid().get()));
                    step("And has admin credentials resolved from a dynamically provisioned admin secret ", () -> {
                        final Secret secret = client.secrets()
                                .loadSecret(entandoDatabaseService, NameUtils.standardAdminSecretName(entandoDatabaseService));
                        assertThat(theVariableReferenceNamed("POSTGRESQL_PASSWORD").on(thePrimaryContainerOn(deployment)).getSecretKeyRef()
                                .getKey())
                                .isEqualTo(SecretUtils.PASSSWORD_KEY);
                        assertThat(theVariableReferenceNamed("POSTGRESQL_PASSWORD").on(thePrimaryContainerOn(deployment)).getSecretKeyRef()
                                .getName())
                                .isEqualTo(secret.getMetadata().getName());
                        assertThat(theVariableReferenceNamed("POSTGRESQL_ADMIN_PASSWORD").on(thePrimaryContainerOn(deployment))
                                .getSecretKeyRef()
                                .getKey())
                                .isEqualTo(SecretUtils.PASSSWORD_KEY);
                        assertThat(theVariableReferenceNamed("POSTGRESQL_ADMIN_PASSWORD").on(thePrimaryContainerOn(deployment))
                                .getSecretKeyRef()
                                .getName())
                                .isEqualTo(secret.getMetadata().getName());
                    });
                });
        step("And the admin secret specifies the standard super user 'postgres' as user and has a dynamically generated password", () -> {
            final Secret secret = client.secrets()
                    .loadSecret(entandoDatabaseService, NameUtils.standardAdminSecretName(entandoDatabaseService));
            attachKubernetesResource("Admin Secret", secret);
            assertThat(theKey("username").on(secret)).isEqualTo("postgres");
            assertThat(theKey("password").on(secret)).isNotBlank();
        });
        step("And a Kubernetes Service was created:", () -> {
            final Service service = client.services()
                    .loadService(entandoDatabaseService, NameUtils.standardServiceName(entandoDatabaseService));
            attachKubernetesResource("Service", service);
            step("Exposing the port 5432 ",
                    () -> assertThat(service.getSpec().getPorts().get(0).getPort()).isEqualTo(5432));
            step("Targeting port 5432 in the Deployment",
                    () -> assertThat(service.getSpec().getPorts().get(0).getTargetPort().getIntVal()).isEqualTo(5432));
            step("And with a label selector matching the labels of the Pod Template on the  Deployment",
                    () -> assertThat(service.getSpec().getSelector()).containsAllEntriesOf(
                            Map.of(LabelNames.RESOURCE_KIND.getName(), "EntandoDatabaseService", "EntandoDatabaseService",
                                    entandoDatabaseService.getMetadata().getName(),
                                    LabelNames.DEPLOYMENT.getName(), entandoDatabaseService.getMetadata().getName())
                    ));
        });

        step("And the resulting DatabaseServiceResult reflects the correct information to connect to the deployed DBMS service", () -> {
            DatabaseConnectionInfo connectionInfo = new ProvidedDatabaseCapability(
                    getClient().entandoResources()
                            .loadCapabilityProvisioningResult(
                                    providedCapability.getStatus().getServerStatus(NameUtils.MAIN_QUALIFIER).get()));
            Allure.attachment("DatabaseServiceResult", SerializationHelper.serialize(connectionInfo));
            assertThat(connectionInfo.getDatabaseName()).isEqualTo("default_dbms_in_namespace_db");
            assertThat(connectionInfo.getPort()).isEqualTo("5432");
            assertThat(connectionInfo.getInternalServiceHostname())
                    .isEqualTo("default-dbms-in-namespace-service.my-namespace.svc.cluster.local");
            assertThat(connectionInfo.getVendor()).isEqualTo(DbmsVendorConfig.POSTGRESQL);
        });
        attachKubernetesState();
    }

    @ParameterizedTest(name = ParameterizedTest.DISPLAY_NAME_PLACEHOLDER + ":[" + ParameterizedTest.ARGUMENTS_WITH_NAMES_PLACEHOLDER + "]")
    @MethodSource("provideMysqlParameters")
    @DisplayName("Should deploy the correct database image with the provided configuration settings for the currently selected compliance"
            + " mode")
    @Description("Should deploy the correct database image with the provided configuration settings for the currently selected compliance"
            + " mode")
    void shouldDeployMysqlWithGivenConfigurationSettings(EntandoOperatorComplianceMode complianceMode,
            DbmsDockerVendorStrategy expectedDbmsStrategy) {
        step("Given that the Entando Operator is running in '" + complianceMode.getName() + "' compliance mode ",
                () -> attachEnvironmentVariable(EntandoOperatorSpiConfigProperty.ENTANDO_K8S_OPERATOR_COMPLIANCE_MODE,
                        complianceMode.getName()));
        step("And the Operator runs in a Kubernetes environment the requires a filesystem user/group override for mounted volumes",
                () -> attachEnvironmentVariable(EntandoOperatorConfigProperty.ENTANDO_REQUIRES_FILESYSTEM_GROUP_OVERRIDE, "true"));
        final Map<String, String> selector = Map.of("my-label", "my-label-value");
        step("When I request an DBMS Capability with all additional parameters provided",
                () -> runControllerAgainstCustomResource(new EntandoDatabaseServiceBuilder()
                        .withNewMetadata()
                        .withNamespace(MY_NAMESPACE)
                        .withName("my-labelled-mysql-dbms")
                        .withLabels(selector)
                        .endMetadata()
                        .withNewSpec()
                        .withCreateDeployment(true)
                        .withJdbcParameters(Map.of("disconnectOnExpiredPasswords", "true"))
                        .withDbms(DbmsVendor.MYSQL)
                        .withProvidedCapabilityScope(CapabilityScope.LABELED)
                        .withDatabaseName("my_db")
                        .endSpec()
                        .build()));
        final ProvidedCapability providedCapability = client.capabilities().providedCapabilityByLabels(selector).get();
        final EntandoDatabaseService entandoDatabaseService = client.entandoResources()
                .load(EntandoDatabaseService.class, providedCapability.getMetadata().getNamespace(),
                        providedCapability.getMetadata().getName());
        step("Then an ProvidedCapability was made available:", () -> {
            step("using the DeployDirectly provisioningStrategy",
                    () -> assertThat(providedCapability.getSpec().getProvisioningStrategy()))
                    .contains(CapabilityProvisioningStrategy.DEPLOY_DIRECTLY);
            step("a PostgreSQL database",
                    () -> assertThat(providedCapability.getSpec().getImplementation()).contains(StandardCapabilityImplementation.MYSQL));
            step("and it is owned by the EntandoDatabaseService to ensure only changes from the EntandoDatabaseService will change the "
                            + "implementing Kubernetes resources",
                    () -> assertThat(ResourceUtils.customResourceOwns(entandoDatabaseService, providedCapability)));

            step("and is labeled with the labels from in the EntandoDatabaseService",
                    () -> assertThat(providedCapability.getMetadata().getLabels()).containsAllEntriesOf(selector));

            step("and the selector is set up to match these labels",
                    () -> assertThat(providedCapability.getSpec().getSelector()).containsAllEntriesOf(selector));

            attachKubernetesResource("ProvidedCapability", providedCapability);
        });
        step("And a Kubernetes Deployment was created reflecting the requirements of the MySQL image:" + expectedDbmsStrategy
                        .getOrganization() + "/" + expectedDbmsStrategy.getImageRepository(),
                () -> {
                    final Deployment deployment = client.deployments()
                            .loadDeployment(entandoDatabaseService, NameUtils.standardDeployment(entandoDatabaseService));
                    attachKubernetesResource("Deployment", deployment);
                    step("using the MySQL Image " + expectedDbmsStrategy.getOrganization() + "/" + expectedDbmsStrategy
                                    .getImageRepository(),
                            () -> assertThat(thePrimaryContainerOn(deployment).getImage())
                                    .contains(expectedDbmsStrategy.getOrganization() + "/" + expectedDbmsStrategy.getImageRepository()));
                    step("With a volume mounted to the standard data directory /var/lib/mysql/data",
                            () -> assertThat(theVolumeMountNamed(providedCapability.getMetadata().getName() + "-db-volume")
                                    .on(thePrimaryContainerOn(deployment))
                                    .getMountPath()).isEqualTo("/var/lib/mysql/data"));
                    step("Which is bound to a PersistentVolumeClain", () -> {
                        final PersistentVolumeClaim pvc = client.persistentVolumeClaims()
                                .loadPersistentVolumeClaim(entandoDatabaseService, "default-dbms-in-namespace-db-pvc");
                        attachKubernetesResource("PersistentVolumeClaim", pvc);
                        assertThat(
                                theVolumeNamed(providedCapability.getMetadata().getName() + "-db-volume").on(deployment)
                                        .getPersistentVolumeClaim()
                                        .getClaimName()).isEqualTo(
                                providedCapability.getMetadata().getName() + "-db-pvc");
                    });
                    step("And livenessProbe, startupProbe and readinessProbe all use the standard PostgreSQL command "
                                    + "/usr/libexec/check-container",
                            () -> {
                                assertThat(thePrimaryContainerOn(deployment).getLivenessProbe().getExec().getCommand())
                                        .contains("MYSQL_PWD=${MYSQL_ROOT_PASSWORD} mysql -h 127.0.0.1 -u root -e 'SELECT 1'");
                                assertThat(thePrimaryContainerOn(deployment).getReadinessProbe().getExec().getCommand())
                                        .contains("MYSQL_PWD=${MYSQL_ROOT_PASSWORD} mysql -h 127.0.0.1 -u root -e 'SELECT 1'");
                                assertThat(thePrimaryContainerOn(deployment).getStartupProbe().getExec().getCommand())
                                        .contains("MYSQL_PWD=${MYSQL_ROOT_PASSWORD} mysql -h 127.0.0.1 -u root -e 'SELECT 1'");
                            });

                    step("And the File System User/Group override " + expectedDbmsStrategy.getFileSystemUserGroupid().get()
                            + "has been applied to the mount", () ->
                            assertThat(deployment.getSpec().getTemplate().getSpec().getSecurityContext().getFsGroup())
                                    .isEqualTo(expectedDbmsStrategy.getFileSystemUserGroupid().get()));
                    step("And has admin credentials resolved from a dynamically provisioned admin secret ", () -> {
                        final Secret secret = client.secrets()
                                .loadSecret(entandoDatabaseService, NameUtils.standardAdminSecretName(entandoDatabaseService));
                        assertThat(theVariableReferenceNamed("MYSQL_ROOT_PASSWORD").on(thePrimaryContainerOn(deployment)).getSecretKeyRef()
                                .getKey())
                                .isEqualTo(SecretUtils.PASSSWORD_KEY);
                    });
                });
        step("And the admin secret specifies the standard super user 'postgres' as user and has a dynamically generated password", () -> {
            final Secret secret = client.secrets()
                    .loadSecret(entandoDatabaseService, NameUtils.standardAdminSecretName(entandoDatabaseService));
            attachKubernetesResource("Admin Secret", secret);
            assertThat(theKey("username").on(secret)).isEqualTo("root");
            assertThat(theKey("password").on(secret)).isNotBlank();
        });
        step("And a Kubernetes Service was created:", () -> {
            final Service service = client.services()
                    .loadService(entandoDatabaseService, NameUtils.standardServiceName(entandoDatabaseService));
            attachKubernetesResource("Service", service);
            step("Exposing the port 3306 ",
                    () -> assertThat(service.getSpec().getPorts().get(0).getPort()).isEqualTo(3306));
            step("Targeting port 3306 in the Deployment",
                    () -> assertThat(service.getSpec().getPorts().get(0).getTargetPort().getIntVal()).isEqualTo(3306));
            step("And with a label selector matching the labels of the Pod Template on the  Deployment",
                    () -> assertThat(service.getSpec().getSelector()).containsAllEntriesOf(
                            Map.of(LabelNames.RESOURCE_KIND.getName(), "EntandoDatabaseService", "EntandoDatabaseService",
                                    entandoDatabaseService.getMetadata().getName(),
                                    LabelNames.DEPLOYMENT.getName(), entandoDatabaseService.getMetadata().getName())
                    ));
        });

        step("And the resulting DatabaseServiceResult reflects the correct information to connect to the deployed DBMS service", () -> {
            DatabaseConnectionInfo connectionInfo = new ProvidedDatabaseCapability(
                    getClient().entandoResources()
                            .loadCapabilityProvisioningResult(
                                    providedCapability.getStatus().getServerStatus(NameUtils.MAIN_QUALIFIER).get()));
            Allure.attachment("DatabaseServiceResult", SerializationHelper.serialize(connectionInfo));
            assertThat(connectionInfo.getDatabaseName()).isEqualTo("my_db");
            assertThat(connectionInfo.getPort()).isEqualTo("3306");
            assertThat(connectionInfo.getInternalServiceHostname())
                    .isEqualTo(providedCapability.getMetadata().getName() + "-service.my-namespace.svc.cluster.local");
            assertThat(connectionInfo.getVendor()).isEqualTo(DbmsVendorConfig.MYSQL);
            assertThat(connectionInfo.getJdbcParameters()).containsEntry("disconnectOnExpiredPasswords", "true");
        });
        attachKubernetesState();
    }
}
