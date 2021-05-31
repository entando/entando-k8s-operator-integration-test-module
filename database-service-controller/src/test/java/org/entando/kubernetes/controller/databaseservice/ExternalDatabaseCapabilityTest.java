/*
 *
 * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 *  This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 */

package org.entando.kubernetes.controller.databaseservice;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretBuilder;
import io.fabric8.kubernetes.api.model.Service;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import java.util.Map;
import org.entando.kubernetes.controller.spi.client.SerializedEntandoResource;
import org.entando.kubernetes.controller.spi.command.SerializationHelper;
import org.entando.kubernetes.controller.spi.common.DbmsVendorConfig;
import org.entando.kubernetes.controller.spi.common.EntandoControllerException;
import org.entando.kubernetes.controller.spi.common.NameUtils;
import org.entando.kubernetes.controller.spi.common.ResourceUtils;
import org.entando.kubernetes.controller.spi.common.SecretUtils;
import org.entando.kubernetes.controller.spi.container.ProvidedDatabaseCapability;
import org.entando.kubernetes.controller.spi.result.DatabaseConnectionInfo;
import org.entando.kubernetes.model.capability.CapabilityProvisioningStrategy;
import org.entando.kubernetes.model.capability.CapabilityRequirement;
import org.entando.kubernetes.model.capability.CapabilityRequirementBuilder;
import org.entando.kubernetes.model.capability.CapabilityScope;
import org.entando.kubernetes.model.capability.ProvidedCapability;
import org.entando.kubernetes.model.capability.StandardCapability;
import org.entando.kubernetes.model.capability.StandardCapabilityImplementation;
import org.entando.kubernetes.model.common.EntandoDeploymentPhase;
import org.entando.kubernetes.model.common.ResourceReference;
import org.entando.kubernetes.model.externaldatabase.EntandoDatabaseService;
import org.entando.kubernetes.test.common.SourceLink;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Tags({@Tag("component"), @Tag("in-process"), @Tag("allure")})
@Feature("As a controller developer, I would like request a capability that will allow me to use an external database service so that I "
        + "can leverage existing database infrastructure")
@SourceLink("ExternalDatabaseCapabilityTest.java")
class ExternalDatabaseCapabilityTest extends DatabaseServiceControllerTestBase {

    public static final String SPECIFIED_DBMS = "specified-dbms";

    @Test
    @Description("Should link to external database service when all required fields are provided")
    void shouldLinkToExternalDatabaseService() {
        step("Given I have configured a secret with admin credentials to a remote database service", () -> {
            final Secret adminSecret = new SecretBuilder()
                    .withNewMetadata()
                    .withNamespace(MY_NAMESPACE)
                    .withName("my-existing-dbms-admin-secret")
                    .endMetadata()
                    .addToData(SecretUtils.USERNAME_KEY, "someuser")
                    .addToData(SecretUtils.PASSSWORD_KEY, "somepassword")
                    .build();
            getClient().secrets().createSecretIfAbsent(newResourceRequiringCapability(), adminSecret);
            attachKubernetesResource("Existing Admin Secret", adminSecret);
        });
        step("When I request an DBMS Capability  with its name and namespace explicitly specified, provisioned externally",
                () -> runControllerAgainstCapabilityRequirement(newResourceRequiringCapability(), new CapabilityRequirementBuilder()
                        .withCapability(StandardCapability.DBMS)
                        .withImplementation(StandardCapabilityImplementation.MYSQL)
                        .withProvisioningStrategy(CapabilityProvisioningStrategy.USE_EXTERNAL)
                        .withCapabilityRequirementScope(CapabilityScope.SPECIFIED)
                        .withNewExternallyProvidedService()
                        .withHost("pg.apps.serv.run")
                        .withPort(3307)
                        .withAdminSecretName("my-existing-dbms-admin-secret")
                        .endExternallyProvidedService()
                        .withSpecifiedCapability(new ResourceReference(MY_NAMESPACE, SPECIFIED_DBMS))
                        .withCapabilityParameters(
                                Map.of(ProvidedDatabaseCapability.DATABASE_NAME_PARAMETER, "my_db",
                                        ProvidedDatabaseCapability.JDBC_PARAMETER_PREFIX + "disconnectOnExpiredPasswords", "true"))
                        .build()));
        final ProvidedCapability providedCapability = client.entandoResources()
                .load(ProvidedCapability.class, MY_NAMESPACE, SPECIFIED_DBMS);
        final EntandoDatabaseService entandoDatabaseService = client.entandoResources()
                .load(EntandoDatabaseService.class, MY_NAMESPACE, SPECIFIED_DBMS);
        step("Then an EntandoDatabaseService was provisioned:", () -> {
            attachKubernetesResource("EntandoDatabaseService", entandoDatabaseService);
            step("with the name explicitly specified", () -> {
                assertThat(entandoDatabaseService.getMetadata().getName()).isEqualTo(SPECIFIED_DBMS);
                assertThat(providedCapability.getMetadata().getName()).isEqualTo(SPECIFIED_DBMS);
                assertThat(providedCapability.getSpec().getSpecifiedCapability().get().getName()).isEqualTo(SPECIFIED_DBMS);
            });

            step("using the 'Use External' provisioningStrategy",
                    () -> assertThat(entandoDatabaseService.getSpec().getCreateDeployment()).contains(Boolean.FALSE));
            step("and it is owned by the ProvidedCapability to ensure only changes from the ProvidedCapability will change the "
                            + "implementing Kubernetes resources",
                    () -> assertThat(ResourceUtils.customResourceOwns(providedCapability, entandoDatabaseService)));
            step("and its host, port and database name reflect the connection info provided in the CapabilityRequirement",
                    () -> {
                        assertThat(entandoDatabaseService.getSpec().getHost()).contains("pg.apps.serv.run");
                        assertThat(entandoDatabaseService.getSpec().getPort()).contains(3307);
                        assertThat(entandoDatabaseService.getSpec().getDatabaseName()).contains("my_db");
                    });
            step("and the ProvidedCapability's status carries the name of the correct admin secret to use",
                    () -> assertThat(providedCapability.getStatus().findCurrentServerStatus().get().getAdminSecretName())
                            .contains("my-existing-dbms-admin-secret"));
        });
        step("And an 'ExternalName' Service  was provisioned:", () -> {
            final Service service = getClient().services()
                    .loadService(entandoDatabaseService, NameUtils.standardServiceName(entandoDatabaseService));
            attachKubernetesResource("Service", service);
            assertThat(service.getSpec().getType()).isEqualTo("ExternalName");
            step("mapped to the port 3307", () -> {
                assertThat(service.getSpec().getPorts().get(0).getPort()).isEqualTo(3307);
            });

            step("and to the previously configured hostname of the database service",
                    () -> assertThat(service.getSpec().getExternalName()).isEqualTo("pg.apps.serv.run"));
        });
        final DatabaseConnectionInfo providedDatabase = new ProvidedDatabaseCapability(
                client.capabilities().buildCapabilityProvisioningResult(providedCapability));
        step("And the provided Database connection info reflects the external service", () -> {
            Allure.attachment("DatabaseConnectionInfo", SerializationHelper.serialize(providedDatabase));
            step("with the port and database name previously specified", () -> {
                assertThat(providedDatabase.getDatabaseName()).isEqualTo("my_db");
                assertThat(providedDatabase.getPort()).isEqualTo("3307");
            });
            step("and a hostname that reflects the previously inspected ExternalName service", () -> {
                assertThat(providedDatabase.getInternalServiceHostname())
                        .isEqualTo("specified-dbms-service.my-namespace.svc.cluster.local");
            });
            step("and previously configured JDBC parameters", () -> {
                assertThat(providedDatabase.getJdbcParameters()).containsAllEntriesOf(Map.of("disconnectOnExpiredPasswords", "true"));
            });
            step("and the previously configured DBMS vendor config", () -> {
                assertThat(providedDatabase.getVendor()).isEqualTo(DbmsVendorConfig.MYSQL);
            });
            step("and the name of the admin secret the was created ", () -> {
                assertThat(providedDatabase.getAdminSecretName()).isEqualTo("my-existing-dbms-admin-secret");
            });
        });
    }

    @Test
    @Description("Should fail when the admin secret specified is absent in the deployment namespace")
    void shouldFailWhenAdminSecretAbsent() {
        step("Given I have configured not configured a secret with admin credentials to a remote database service");
        step("When I request an DBMS Capability that is externally provided to a non-existing admin secret");
        step("Then an EntandoControllerException is thrown by the CapabilityProvider", () -> {
            final SerializedEntandoResource forResource = newResourceRequiringCapability();
            final CapabilityRequirement build = new CapabilityRequirementBuilder()
                    .withCapability(StandardCapability.DBMS)
                    .withProvisioningStrategy(CapabilityProvisioningStrategy.USE_EXTERNAL)
                    .withCapabilityRequirementScope(CapabilityScope.NAMESPACE)
                    .withNewExternallyProvidedService()
                    .withPath("/auth")
                    .withHost("kc.apps.serv.run")
                    .withPort(8080)
                    .withAdminSecretName("my-existing-dbms-admin-secret")
                    .endExternallyProvidedService()
                    .build();
            assertThrows(EntandoControllerException.class,
                    () -> runControllerAgainstCapabilityRequirement(forResource, build));
        });
        final ProvidedCapability providedCapability = client.entandoResources()
                .load(ProvidedCapability.class, MY_NAMESPACE, DEFAULT_DBMS_IN_NAMESPACE);
        final EntandoDatabaseService entandoDatabaseService = client.entandoResources()
                .load(EntandoDatabaseService.class, MY_NAMESPACE, DEFAULT_DBMS_IN_NAMESPACE);
        step("And the resulting status objects of both the ProvidedCapability and EntandoDatabaseService reflect the failure and the cause"
                        + " for the failure",
                () -> {
                    attachKubernetesResource("EntandoDatabaseService.status", entandoDatabaseService.getStatus());
                    attachKubernetesResource("ProvidedCapability.status", providedCapability.getStatus());
                    step("The phase of the statuses of both the ProvidedCapability and EntandoDatabaseService is FAILED", () -> {
                        assertThat(entandoDatabaseService.getStatus().getPhase()).isEqualTo(EntandoDeploymentPhase.FAILED);
                        assertThat(providedCapability.getStatus().getPhase()).isEqualTo(EntandoDeploymentPhase.FAILED);
                    });
                    step("And the statuses of  both the ProvidedCapability and EntandoDatabaseService reflect the correct error message",
                            () -> {
                                assertThat(entandoDatabaseService.getStatus().findCurrentServerStatus().get().getEntandoControllerFailure()
                                        .getDetailMessage()).contains(
                                        "Please ensure that a secret with the name 'my-existing-dbms-admin-secret' exists in the requested"
                                                + " namespace");
                                assertThat(providedCapability.getStatus().findCurrentServerStatus().get().getEntandoControllerFailure()
                                        .getDetailMessage()).contains(
                                        "Please ensure that a secret with the name 'my-existing-dbms-admin-secret' exists in the requested"
                                                + " namespace");
                            });
                });
    }

    @Test
    @Description("Should fail when no host name is specified")
    void shouldFailWhenNoHostNameSpecified() {
        final SerializedEntandoResource forResource = newResourceRequiringCapability();
        step("Given I have configured a secret with admin credentials to a remote database service", () -> {
            final Secret adminSecret = new SecretBuilder()
                    .withNewMetadata()
                    .withNamespace(MY_NAMESPACE)
                    .withName("my-existing-dbms-admin-secret")
                    .endMetadata()
                    .addToData(SecretUtils.USERNAME_KEY, "someuser")
                    .addToData(SecretUtils.PASSSWORD_KEY, "somepassword")
                    .build();
            getClient().secrets().createSecretIfAbsent(forResource, adminSecret);
            attachKubernetesResource("Existing Admin Secret", adminSecret);
        });
        step("When I request a Database Capability that is externally provided to a non-existing admin secret");
        step("Then an EntandoControllerException is thrown by the CapabilityProvider", () -> {
            final CapabilityRequirement capabilityRequirement = new CapabilityRequirementBuilder()
                    .withCapability(StandardCapability.DBMS)
                    .withProvisioningStrategy(CapabilityProvisioningStrategy.USE_EXTERNAL)
                    .withCapabilityRequirementScope(CapabilityScope.NAMESPACE)
                    .withNewExternallyProvidedService()
                    .withHost(null)//NO HOST!!!
                    .withPort(5432)
                    .withAdminSecretName("my-existing-dbms-admin-secret")
                    .endExternallyProvidedService()
                    .build();
            assertThrows(EntandoControllerException.class,
                    () -> runControllerAgainstCapabilityRequirement(forResource, capabilityRequirement));
        });
        final ProvidedCapability providedCapability = client.entandoResources()
                .load(ProvidedCapability.class, MY_NAMESPACE, DEFAULT_DBMS_IN_NAMESPACE);
        final EntandoDatabaseService entandoDatabaseService = client.entandoResources()
                .load(EntandoDatabaseService.class, MY_NAMESPACE, DEFAULT_DBMS_IN_NAMESPACE);
        step("And the resulting status objects of both the ProvidedCapability and EntandoDatabaseService reflect the failure and the cause"
                        + " for the failure",
                () -> {
                    attachKubernetesResource("EntandoDatabaseService.status", entandoDatabaseService.getStatus());
                    attachKubernetesResource("ProvidedCapability.status", providedCapability.getStatus());
                    step("The phase of the statuses of both the ProvidedCapability and EntandoDatabaseService is FAILED", () -> {
                        assertThat(entandoDatabaseService.getStatus().getPhase()).isEqualTo(EntandoDeploymentPhase.FAILED);
                        assertThat(providedCapability.getStatus().getPhase()).isEqualTo(EntandoDeploymentPhase.FAILED);
                    });
                    step("And the statuses of  both the ProvidedCapability and EntandoDatabaseService reflect the correct error message",
                            () -> {
                                assertThat(entandoDatabaseService.getStatus().findCurrentServerStatus().get().getEntandoControllerFailure()
                                        .getDetailMessage()).contains(
                                        "Please provide the hostname of the database service you intend to connect to");
                                assertThat(providedCapability.getStatus().findCurrentServerStatus().get().getEntandoControllerFailure()
                                        .getDetailMessage()).contains(
                                        "Please provide the hostname of the database service you intend to connect to");
                            });
                });
    }

    @Test
    @Description("Should fail when no admin secret name is specified")
    void shouldFailWhenNoAdminSecretName() {
        final SerializedEntandoResource forResource = newResourceRequiringCapability();
        step("Given I have configured a secret with admin credentials to a remote database service", () -> {
            final Secret adminSecret = new SecretBuilder()
                    .withNewMetadata()
                    .withNamespace(MY_NAMESPACE)
                    .withName("my-existing-dbms-admin-secret")
                    .endMetadata()
                    .addToData(SecretUtils.USERNAME_KEY, "someuser")
                    .addToData(SecretUtils.PASSSWORD_KEY, "somepassword")
                    .build();
            getClient().secrets().createSecretIfAbsent(forResource, adminSecret);
            attachKubernetesResource("Existing Admin Secret", adminSecret);
        });
        step("When I request an DBMS Capability that is externally provided to a non-existing admin secret");
        step("Then an EntandoControllerException is thrown by the CapabilityProvider", () -> {
            final CapabilityRequirement capabilityRequirement = new CapabilityRequirementBuilder()
                    .withCapability(StandardCapability.DBMS)
                    .withProvisioningStrategy(CapabilityProvisioningStrategy.USE_EXTERNAL)
                    .withCapabilityRequirementScope(CapabilityScope.NAMESPACE)
                    .withNewExternallyProvidedService()
                    .withHost("pghost.com")
                    .withPort(5432)
                    .withAdminSecretName(null)//NO ADMIN SECRET!!
                    .endExternallyProvidedService()
                    .build();
            assertThrows(EntandoControllerException.class,
                    () -> runControllerAgainstCapabilityRequirement(forResource, capabilityRequirement));
        });
        final ProvidedCapability providedCapability = client.entandoResources()
                .load(ProvidedCapability.class, MY_NAMESPACE, DEFAULT_DBMS_IN_NAMESPACE);
        final EntandoDatabaseService entandoDatabaseService = client.entandoResources()
                .load(EntandoDatabaseService.class, MY_NAMESPACE, DEFAULT_DBMS_IN_NAMESPACE);
        step("And the resulting status objects of both the ProvidedCapability and EntandoDatabaseService reflect the failure and the cause"
                        + " for the failure",
                () -> {
                    attachKubernetesResource("EntandoDatabaseService.status", entandoDatabaseService.getStatus());
                    attachKubernetesResource("ProvidedCapability.status", providedCapability.getStatus());
                    step("The phase of the statuses of both the ProvidedCapability and EntandoDatabaseService is FAILED", () -> {
                        assertThat(entandoDatabaseService.getStatus().getPhase()).isEqualTo(EntandoDeploymentPhase.FAILED);
                        assertThat(providedCapability.getStatus().getPhase()).isEqualTo(EntandoDeploymentPhase.FAILED);
                    });
                    step("And the statuses of  both the ProvidedCapability and EntandoDatabaseService reflect the correct error message",
                            () -> {
                                assertThat(entandoDatabaseService.getStatus().findCurrentServerStatus().get().getEntandoControllerFailure()
                                        .getDetailMessage()).contains(
                                        "Please provide the name of the secret containing the admin credentials for the database service "
                                                + "you intend to connect to");
                                assertThat(providedCapability.getStatus().findCurrentServerStatus().get().getEntandoControllerFailure()
                                        .getDetailMessage()).contains(
                                        "Please provide the name of the secret containing the admin credentials for the database service "
                                                + "you intend to connect to");
                            });
                });
    }
}
