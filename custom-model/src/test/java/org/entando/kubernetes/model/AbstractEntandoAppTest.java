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

package org.entando.kubernetes.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import io.fabric8.kubernetes.client.CustomResourceList;
import io.fabric8.kubernetes.client.dsl.internal.CustomResourceOperationsImpl;
import java.util.Collections;
import org.entando.kubernetes.model.app.DoneableEntandoApp;
import org.entando.kubernetes.model.app.EntandoApp;
import org.entando.kubernetes.model.app.EntandoAppBuilder;
import org.entando.kubernetes.model.gitspec.GitResponsibility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class AbstractEntandoAppTest implements CustomResourceTestUtil {

    public static final String MY_CUSTOM_SERVER_IMAGE = "somenamespace/someimage:3.2.2";
    public static final String MY_CLUSTER_INFRASTRUCTURE = "my-cluster-infrastructure";
    public static final String PARAM_VALUE = "my-value";
    public static final String PARAM_NAME = "MY_PARAM";
    public static final String MY_GIT_SECRET = "my-git-secret";
    public static final String MY_BACKUP_GIT_REPO = "https://github.com/entando/doesnoexist.git";
    protected static final String MY_APP = "my-app";
    protected static final String MY_NAMESPACE = TestConfig.calculateNameSpace("my-namespace");
    private static final String ENTANDO_IMAGE_VERSION = "6.1.0-SNAPSHOT";
    private static final String MYINGRESS_COM = "myingress.com";
    private static final String MY_INGRESS_PATH = "/my-ingress-path";
    private static final String MY_KEYCLOAK_SECRET = "my-keycloak-secret";
    private static final String MY_VALUE = "my-value";
    private static final String MY_LABEL = "my-label";
    private static final String MY_TLS_SECRET = "my-tls-secret";
    private static final Integer MY_REPLICAS = 5;
    public static final String MY_SERVICE_ACCOUNT = "myServiceAccount";
    private EntandoResourceOperationsRegistry registry;

    @BeforeEach
    public void deleteEntandoApps() {
        this.registry = new EntandoResourceOperationsRegistry(getClient());
        prepareNamespace(entandoApps(), MY_NAMESPACE);
    }

    @Test
    public void testCreateEntandoApp() {
        //Given
        EntandoApp entandoApp = new EntandoAppBuilder()
                .withNewMetadata().withName(MY_APP)
                .withNamespace(MY_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .withDbms(DbmsVendor.MYSQL)
                .withCustomServerImage(MY_CUSTOM_SERVER_IMAGE)
                .withEntandoImageVersion(ENTANDO_IMAGE_VERSION)
                .withStandardServerImage(JeeServer.WILDFLY)
                .withReplicas(MY_REPLICAS)
                .withTlsSecretName(MY_TLS_SECRET)
                .withIngressHostName(MYINGRESS_COM)
                .withKeycloakSecretToUse(MY_KEYCLOAK_SECRET)
                .withClusterInfrastructureToUse(MY_CLUSTER_INFRASTRUCTURE)
                .withIngressPath(MY_INGRESS_PATH)
                .withNewBackupGitSpec()
                .withRepository(MY_BACKUP_GIT_REPO)
                .withSecretName(MY_GIT_SECRET)
                .withResponsibility(GitResponsibility.PUSH)
                .withTargertRef("master")
                .endBackupGitSpec()
                .withIngressPath(MY_INGRESS_PATH)
                .addNewParameter(PARAM_NAME, PARAM_VALUE)
                .endSpec()
                .build();
        entandoApps().inNamespace(MY_NAMESPACE).createNew().withMetadata(entandoApp.getMetadata()).withSpec(entandoApp.getSpec()).done();
        //When
        EntandoApp actual = entandoApps().inNamespace(MY_NAMESPACE).withName(MY_APP).get();
        //Then
        assertThat(actual.getSpec().getDbms().get(), is(DbmsVendor.MYSQL));
        assertThat(actual.getSpec().getEntandoImageVersion().get(), is(ENTANDO_IMAGE_VERSION));
        assertThat(actual.getSpec().getIngressHostName().get(), is(MYINGRESS_COM));
        assertThat(actual.getSpec().getKeycloakSecretToUse().get(), is(MY_KEYCLOAK_SECRET));
        assertThat(actual.getTlsSecretName().get(), is(MY_TLS_SECRET));
        assertThat(actual.getIngressHostName().get(), is(MYINGRESS_COM));
        assertThat(actual.getSpec().getIngressPath().get(), is(MY_INGRESS_PATH));
        assertThat(actual.getKeycloakSecretToUse().get(), is(MY_KEYCLOAK_SECRET));
        assertThat(actual.getSpec().getStandardServerImage().get(), is(JeeServer.WILDFLY));
        assertThat(actual.getSpec().getReplicas().get(), is(5));
        assertThat(actual.getSpec().getTlsSecretName().get(), is(MY_TLS_SECRET));
        assertThat(actual.getSpec().getCustomServerImage().isPresent(), is(false));//because it was overridden by a standard image
        assertThat(actual.getSpec().getClusterInfrastructureTouse().get(), is(MY_CLUSTER_INFRASTRUCTURE));
        assertThat(actual.getSpec().getBackupGitSpec().get().getRepository(), is(MY_BACKUP_GIT_REPO));
        assertThat(actual.getSpec().getBackupGitSpec().get().getSecretName().get(), is(MY_GIT_SECRET));
        assertThat(actual.getSpec().getBackupGitSpec().get().getResponsibility(), is(GitResponsibility.PUSH));
        assertThat(actual.getSpec().getBackupGitSpec().get().getTargetRef().get(), is("master"));
        assertThat(actual.getSpec().getParameters().get(PARAM_NAME), is(PARAM_VALUE));
        assertThat(actual.getMetadata().getName(), is(MY_APP));
    }

    @Test
    public void testEditEntandoApp() {
        //Given
        EntandoApp entandoApp = new EntandoAppBuilder()
                .withNewMetadata()
                .withName(MY_APP)
                .withNamespace(MY_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .withDbms(DbmsVendor.POSTGRESQL)
                .withEntandoImageVersion("6.2.0-SNAPSHOT")
                .withCustomServerImage("asdfasdf/asdf:2")
                .withStandardServerImage(JeeServer.WILDFLY)
                .withReplicas(4)
                .withTlsSecretName("another-tls-secret")
                .withIngressHostName("anotheringress.com")
                .withKeycloakSecretToUse("another-keycloak-secret")
                .addNewParameter("anotherparam", "123123")
                .withClusterInfrastructureToUse("some-cluster-infrastructure")
                .withNewBackupGitSpec()
                .withRepository("somerip.git")
                .withSecretName("some-secert")
                .withResponsibility(GitResponsibility.PULL)
                .withTargertRef("pr1")
                .endBackupGitSpec()
                .withServiceAccountToUse("someserviceacount")
                .endSpec()
                .build();
        //When
        //We are not using the mock server here because of a known bug
        EntandoApp actual = editEntandoApp(entandoApp)
                .editMetadata().addToLabels(MY_LABEL, MY_VALUE)
                .endMetadata()
                .editSpec()
                .withDbms(DbmsVendor.MYSQL)
                .withEntandoImageVersion(ENTANDO_IMAGE_VERSION)
                .withStandardServerImage(JeeServer.WILDFLY)
                .withCustomServerImage(MY_CUSTOM_SERVER_IMAGE)
                .withReplicas(5)
                .withTlsSecretName(MY_TLS_SECRET)
                .withIngressHostName(MYINGRESS_COM)
                .withKeycloakSecretToUse(MY_KEYCLOAK_SECRET)
                .withClusterInfrastructureToUse(MY_CLUSTER_INFRASTRUCTURE)
                .withNewBackupGitSpec()
                .withRepository(MY_BACKUP_GIT_REPO)
                .withSecretName(MY_GIT_SECRET)
                .withResponsibility(GitResponsibility.PUSH)
                .withTargertRef("master")
                .endBackupGitSpec()
                .withServiceAccountToUse(MY_SERVICE_ACCOUNT)
                .withParameters(Collections.singletonMap(PARAM_NAME, PARAM_VALUE))
                .endSpec()
                .withStatus(new WebServerStatus("some-qualifier"))
                .withStatus(new DbServerStatus("another-qualifier"))
                .withPhase(EntandoDeploymentPhase.STARTED)
                .done();
        //Then
        assertThat(actual.getSpec().getDbms().get(), is(DbmsVendor.MYSQL));
        assertThat(actual.getSpec().getEntandoImageVersion().get(), is(ENTANDO_IMAGE_VERSION));
        assertThat(actual.getSpec().getIngressHostName().get(), is(MYINGRESS_COM));
        assertThat(actual.getSpec().getKeycloakSecretToUse().get(), is(MY_KEYCLOAK_SECRET));
        assertThat(actual.getSpec().getStandardServerImage().isPresent(), is(false));//overridden by customServerImage
        assertThat(actual.getSpec().getCustomServerImage().get(), is(MY_CUSTOM_SERVER_IMAGE));
        assertThat(actual.getSpec().getClusterInfrastructureTouse().get(), is(MY_CLUSTER_INFRASTRUCTURE));
        assertThat(actual.getSpec().getReplicas().get(), is(5));
        assertThat(actual.getSpec().getTlsSecretName().get(), is(MY_TLS_SECRET));
        assertThat(actual.getSpec().getServiceAccountToUse().get(), is(MY_SERVICE_ACCOUNT));
        assertThat(actual.getSpec().getBackupGitSpec().get().getRepository(), is(MY_BACKUP_GIT_REPO));
        assertThat(actual.getSpec().getBackupGitSpec().get().getSecretName().get(), is(MY_GIT_SECRET));
        assertThat(actual.getSpec().getBackupGitSpec().get().getResponsibility(), is(GitResponsibility.PUSH));
        assertThat(actual.getSpec().getBackupGitSpec().get().getTargetRef().get(), is("master"));
        assertThat(actual.getSpec().getParameters().get(PARAM_NAME), is(PARAM_VALUE));
        assertThat(actual.getMetadata().getLabels().get(MY_LABEL), is(MY_VALUE));
    }

    protected DoneableEntandoApp editEntandoApp(EntandoApp entandoApp) {
        entandoApps().inNamespace(MY_NAMESPACE).create(entandoApp);
        return entandoApps().inNamespace(MY_NAMESPACE).withName(MY_APP).edit();
    }

    protected CustomResourceOperationsImpl<EntandoApp, CustomResourceList<EntandoApp>, DoneableEntandoApp> entandoApps() {
        return registry.getOperations(EntandoApp.class);
    }

}
