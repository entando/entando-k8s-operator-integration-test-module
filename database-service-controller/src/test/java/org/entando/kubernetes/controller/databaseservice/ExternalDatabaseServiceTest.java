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
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import org.entando.kubernetes.controller.spi.common.ResourceUtils;
import org.entando.kubernetes.controller.spi.common.SecretUtils;
import org.entando.kubernetes.controller.spi.container.ProvidedDatabaseCapability;
import org.entando.kubernetes.model.capability.CapabilityProvisioningStrategy;
import org.entando.kubernetes.model.capability.ProvidedCapability;
import org.entando.kubernetes.model.common.DbmsVendor;
import org.entando.kubernetes.model.common.EntandoDeploymentPhase;
import org.entando.kubernetes.model.externaldatabase.EntandoDatabaseService;
import org.entando.kubernetes.model.externaldatabase.EntandoDatabaseServiceBuilder;
import org.entando.kubernetes.test.common.SourceLink;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import picocli.CommandLine;

@ExtendWith(MockitoExtension.class)
@Tags({@Tag("component"), @Tag("in-process"), @Tag("allure")})
@Feature(
        "As a controller developer, I would like deploy an EntandoDatabaseService that merely references an externally provisioned "
                + "database "
                + "service so that I "
                + "can leverage an existing user database")
@SourceLink("ExternalDatabaseServiceTest.java")
class ExternalDatabaseServiceTest extends DatabaseServiceControllerTestBase {

    public static final String MY_EXTERNAL_DBMS = "my-external-dbms";

    @Test
    @Description("Should link to external service when all required fields are provided")
    void shouldLinkToExternalService() {
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
            attacheKubernetesResource("Existing Admin Secret", adminSecret);
        });
        step("When I request an DBMS Capability  with its name and namespace explicitly specified, provisioned externally",
                () -> runControllerAgainst(new EntandoDatabaseServiceBuilder()
                        .withNewMetadata()
                        .withName(MY_EXTERNAL_DBMS)
                        .withNamespace(MY_NAMESPACE)
                        .endMetadata()
                        .editSpec()
                        .withCreateDeployment(false)
                        .withDatabaseName("my_db")
                        .withDbms(DbmsVendor.POSTGRESQL)
                        .withDatabaseName("my_db")
                        .withHost("pg.apps.serv.run")
                        .withSecretName("my-existing-dbms-admin-secret")
                        .endSpec()
                        .build()));
        final ProvidedCapability providedCapability = client.entandoResources()
                .load(ProvidedCapability.class, MY_NAMESPACE, MY_EXTERNAL_DBMS);
        final EntandoDatabaseService entandoDatabaseService = client.entandoResources()
                .load(EntandoDatabaseService.class, MY_NAMESPACE, MY_EXTERNAL_DBMS);
        step("Then ProvidedCapability was made available:", () -> {
            attacheKubernetesResource("EntandoDatabaseService", providedCapability);
            step("using the 'Use External' provisioningStrategy",
                    () -> assertThat(providedCapability.getSpec().getProvisioningStrategy()).contains(
                            CapabilityProvisioningStrategy.USE_EXTERNAL));
            step("and it is owned by the EntandoDatabaseService to ensure only changes from the EntandoDatabaseService will change the "
                            + "implementing Kubernetes resources",
                    () -> assertThat(ResourceUtils.customResourceOwns(entandoDatabaseService, providedCapability)));
            step("and its the specified externally provisioned service object reflects the connection info provided in the "
                            + "CapabilityRequirement",
                    () -> {
                        assertThat(providedCapability.getSpec().getExternallyProvisionedService().get().getHost())
                                .isEqualTo("pg.apps.serv.run");
                        assertThat(providedCapability.getSpec().getExternallyProvisionedService().get().getPort()).contains(5432);
                        assertThat(providedCapability.getSpec().getExternallyProvisionedService().get().getAdminSecretName())
                                .isEqualTo("my-existing-dbms-admin-secret");
                    });
        });
        final ProvidedDatabaseCapability providedDatabaseService = new ProvidedDatabaseCapability(
                client.capabilities().buildCapabilityProvisioningResult(providedCapability));
        step("And the provided DatabaseService connection info reflects the external service", () -> {
            assertThat(providedDatabaseService.getPort()).isEqualTo("5432");
            assertThat(providedDatabaseService.getInternalServiceHostname())
                    .isEqualTo("my-external-dbms-service.my-namespace.svc.cluster.local");
            assertThat(providedDatabaseService.getDatabaseName()).isEqualTo("my_db");
            assertThat(providedDatabaseService.getAdminSecretName()).isEqualTo("my-existing-dbms-admin-secret");
        });
    }

    @Test
    @Description("Should fail when the admin secret specified is absent in the deployment namespace")
    void shouldFailWhenAdminSecretAbsent() {
        step("Given I have not configured a secret with admin credentials to a remote database service");
        step("When I request an DBMS Capability that is externally provided to a non-existing admin secret");
        step("Then an IllegalState exception is thrown by the CapabilityProvider", () ->
                assertThrows(CommandLine.ExecutionException.class,
                        () -> runControllerAgainst(new EntandoDatabaseServiceBuilder()
                                .withNewMetadata()
                                .withName(MY_EXTERNAL_DBMS)
                                .withNamespace(MY_NAMESPACE)
                                .endMetadata()
                                .editSpec()
                                .withDbms(DbmsVendor.POSTGRESQL)
                                .withDatabaseName("my_db")
                                .withCreateDeployment(false)
                                .withHost("pg.apps.serv.run")
                                .withSecretName("my-existing-dbms-admin-secret")
                                .endSpec()
                                .build())));
        final ProvidedCapability providedCapability = client.entandoResources()
                .load(ProvidedCapability.class, MY_NAMESPACE, MY_EXTERNAL_DBMS);
        final EntandoDatabaseService entandoDatabaseService = client.entandoResources()
                .load(EntandoDatabaseService.class, MY_NAMESPACE, MY_EXTERNAL_DBMS);
        step("And the resulting status objects of both the ProvidedCapability and EntandoDatabaseService reflect the failure and the cause"
                        + " for the failure",
                () -> {
                    attacheKubernetesResource("EntandoDatabaseService.status", entandoDatabaseService.getStatus());
                    attacheKubernetesResource("ProvidedCapability.status", providedCapability.getStatus());
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
    @Description("Should fail when no host is specified")
    void shouldFailWhenNoHostSpecified() {
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
            attacheKubernetesResource("Existing Admin Secret", adminSecret);
        });
        step("When I request an DBMS Capability that is externally provided to a non-existing admin secret");
        step("Then an IllegalState exception is thrown by the CapabilityProvider", () ->
                assertThrows(CommandLine.ExecutionException.class,
                        () -> runControllerAgainst(new EntandoDatabaseServiceBuilder()
                                .withNewMetadata()
                                .withName(MY_EXTERNAL_DBMS)
                                .withNamespace(MY_NAMESPACE)
                                .endMetadata()
                                .editSpec()
                                .withDatabaseName("my_db")
                                .withDbms(DbmsVendor.POSTGRESQL)
                                .withCreateDeployment(false)
                                .withHost(null)//NO HOST
                                .withSecretName("my-existing-dbms-admin-secret")
                                .endSpec()
                                .build())));
        final ProvidedCapability providedCapability = client.entandoResources()
                .load(ProvidedCapability.class, MY_NAMESPACE, MY_EXTERNAL_DBMS);
        final EntandoDatabaseService entandoDatabaseService = client.entandoResources()
                .load(EntandoDatabaseService.class, MY_NAMESPACE, MY_EXTERNAL_DBMS);
        step("And the resulting status objects of both the ProvidedCapability and EntandoDatabaseService reflect the failure and the cause"
                        + " for the failure",
                () -> {
                    attacheKubernetesResource("EntandoDatabaseService.status", entandoDatabaseService.getStatus());
                    attacheKubernetesResource("ProvidedCapability.status", providedCapability.getStatus());
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
    @Description("Should fail when no database name is specified")
    void shouldFailWhenNoDatabaseSpecified() {
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
            attacheKubernetesResource("Existing Admin Secret", adminSecret);
        });
        step("When I request an DBMS Capability that is externally provided to a non-existing admin secret");
        step("Then an IllegalState exception is thrown by the CapabilityProvider", () ->
                assertThrows(CommandLine.ExecutionException.class,
                        () -> runControllerAgainst(new EntandoDatabaseServiceBuilder()
                                .withNewMetadata()
                                .withName(MY_EXTERNAL_DBMS)
                                .withNamespace(MY_NAMESPACE)
                                .endMetadata()
                                .editSpec()
                                .withDatabaseName(null)//NO DATABASE
                                .withDbms(DbmsVendor.POSTGRESQL)
                                .withCreateDeployment(false)
                                .withHost("pg.apps.serv.run")
                                .withSecretName("my-existing-dbms-admin-secret")
                                .endSpec()
                                .build())));
        final ProvidedCapability providedCapability = client.entandoResources()
                .load(ProvidedCapability.class, MY_NAMESPACE, MY_EXTERNAL_DBMS);
        final EntandoDatabaseService entandoDatabaseService = client.entandoResources()
                .load(EntandoDatabaseService.class, MY_NAMESPACE, MY_EXTERNAL_DBMS);
        step("And the resulting status objects of both the ProvidedCapability and EntandoDatabaseService reflect the failure and the cause"
                        + " for the failure",
                () -> {
                    attacheKubernetesResource("EntandoDatabaseService.status", entandoDatabaseService.getStatus());
                    attacheKubernetesResource("ProvidedCapability.status", providedCapability.getStatus());
                    step("The phase of the statuses of both the ProvidedCapability and EntandoDatabaseService is FAILED", () -> {
                        assertThat(entandoDatabaseService.getStatus().getPhase()).isEqualTo(EntandoDeploymentPhase.FAILED);
                        assertThat(providedCapability.getStatus().getPhase()).isEqualTo(EntandoDeploymentPhase.FAILED);
                    });
                    step("And the statuses of  both the ProvidedCapability and EntandoDatabaseService reflect the correct error message",
                            () -> {
                                assertThat(entandoDatabaseService.getStatus().findCurrentServerStatus().get().getEntandoControllerFailure()
                                        .getDetailMessage()).contains(
                                        "Please provide the name of the database on the database service you intend to connect to");
                                assertThat(providedCapability.getStatus().findCurrentServerStatus().get().getEntandoControllerFailure()
                                        .getDetailMessage()).contains(
                                        "Please provide the name of the database on the database service you intend to connect to");
                            });
                });
    }

    @Test
    @Description("Should fail when no admin secret name is specified")
    void shouldFailWhenNoAdminSecretName() {
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
            attacheKubernetesResource("Existing Admin Secret", adminSecret);
        });
        step("When I request an database Capability that is externally provided to a non-existing admin secret");
        step("Then an IllegalState exception is thrown by the CapabilityProvider", () ->
                assertThrows(CommandLine.ExecutionException.class,
                        () -> runControllerAgainst(new EntandoDatabaseServiceBuilder()
                                .withNewMetadata()
                                .withName(MY_EXTERNAL_DBMS)
                                .withNamespace(MY_NAMESPACE)
                                .endMetadata()
                                .editSpec()
                                .withDbms(DbmsVendor.POSTGRESQL)
                                .withCreateDeployment(false)
                                .withHost("pg.apps.serv.run")
                                .withDatabaseName("my_db")
                                .withSecretName(null)//NO ADMIN SECRET
                                .endSpec()
                                .build())));
        final ProvidedCapability providedCapability = client.entandoResources()
                .load(ProvidedCapability.class, MY_NAMESPACE, MY_EXTERNAL_DBMS);
        final EntandoDatabaseService entandoDatabaseService = client.entandoResources()
                .load(EntandoDatabaseService.class, MY_NAMESPACE, MY_EXTERNAL_DBMS);
        step("And the resulting status objects of both the ProvidedCapability and EntandoDatabaseService reflect the failure and the cause"
                        + " for the failure",
                () -> {
                    attacheKubernetesResource("EntandoDatabaseService.status", entandoDatabaseService.getStatus());
                    attacheKubernetesResource("ProvidedCapability.status", providedCapability.getStatus());
                    step("The phase of the statuses of both the ProvidedCapability and EntandoDatabaseService is FAILED", () -> {
                        assertThat(entandoDatabaseService.getStatus().getPhase()).isEqualTo(EntandoDeploymentPhase.FAILED);
                        assertThat(providedCapability.getStatus().getPhase()).isEqualTo(EntandoDeploymentPhase.FAILED);
                    });
                    step("And the statuses of  both the ProvidedCapability and EntandoDatabaseService reflect the correct error message",
                            () -> {
                                assertThat(entandoDatabaseService.getStatus().findCurrentServerStatus().get().getEntandoControllerFailure()
                                        .getDetailMessage()).contains(
                                        "Please provide the name of the secret containing the admin credentials for the database service "
                                                + "you "
                                                + "intend to "
                                                + "connect to");
                                assertThat(providedCapability.getStatus().findCurrentServerStatus().get().getEntandoControllerFailure()
                                        .getDetailMessage()).contains(
                                        "Please provide the name of the secret containing the admin credentials for the database service "
                                                + "you "
                                                + "intend to "
                                                + "connect to");
                            });
                });
    }
}
