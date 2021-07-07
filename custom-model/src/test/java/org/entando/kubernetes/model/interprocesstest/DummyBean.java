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

package org.entando.kubernetes.model.interprocesstest;

import static org.entando.kubernetes.model.plugin.PluginSecurityLevel.STRICT;

import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.quarkus.runtime.StartupEvent;
import java.util.Arrays;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.entando.kubernetes.model.app.EntandoApp;
import org.entando.kubernetes.model.app.EntandoAppBuilder;
import org.entando.kubernetes.model.common.DbmsVendor;
import org.entando.kubernetes.model.common.EntandoControllerFailureBuilder;
import org.entando.kubernetes.model.common.EntandoCustomResource;
import org.entando.kubernetes.model.common.EntandoDeploymentPhase;
import org.entando.kubernetes.model.common.JeeServer;
import org.entando.kubernetes.model.common.ServerStatus;
import org.entando.kubernetes.model.externaldatabase.EntandoDatabaseService;
import org.entando.kubernetes.model.externaldatabase.EntandoDatabaseServiceBuilder;
import org.entando.kubernetes.model.keycloakserver.EntandoKeycloakServer;
import org.entando.kubernetes.model.keycloakserver.EntandoKeycloakServerBuilder;
import org.entando.kubernetes.model.link.EntandoAppPluginLink;
import org.entando.kubernetes.model.link.EntandoAppPluginLinkBuilder;
import org.entando.kubernetes.model.plugin.EntandoPlugin;
import org.entando.kubernetes.model.plugin.EntandoPluginBuilder;

/**
 * This bean is intended solely to test our custom resources in Quarkus/Graalvm. It is deactivated by default. To activated it for testing
 * purposes, uncomment the @Observes annotation in {@link #onStartup(StartupEvent)}
 */
@Singleton
class DummyBean {

    private static final String MY_EXTERNAL_DATABASE = "my-external-database";
    private static final String MY_DB = "my_db";
    private static final String MYHOST_COM = "myhost.com";
    private static final int PORT_1521 = 1521;
    private static final String MY_DB_SECRET = "my-db-secret";
    private static final String MY_APP_NAMESPACE = "my-app-namespace";
    private static final String MY_PLUGIN_NAMEPSACE = "my-plugin-namepsace";
    private static final String MY_ENTANDO_CLUSTER_INFRASTRUCTURE = "my-entando-cluster-infrastructure";
    private static final String MY_CUSTOM_SERVER_IMAGE = "somenamespace/someimage:3.2.2";
    private static final String MY_CLUSTER_INFRASTRUCTURE = "my-cluster-infrastructure";
    private static final String MY_NAMESPACE = "my-namespace";
    private static final String MY_APP = "my-app";
    private static final String ENTANDO_IMAGE_VERSION = "6.1.0-SNAPSHOT";
    private static final String MYINGRESS_COM = "myingress.com";
    private static final String MY_KEYCLOAK_NAME = "my-keycloak-name";
    private static final String MY_KEYCLOAK_REALM = "my-keycloak-realm";
    private static final String MY_KEYCLOAK_NAME_SPACE = "my-keycloak-namespace";
    private static final String MY_VALUE = "my-value";
    private static final String MY_LABEL = "my-label";
    private static final String MY_TLS_SECRET = "my-tls-secret";
    private static final String MY_PLUGIN = "my-plugin";
    private static final String IMAGE = "entando/someplugin:1.0.2";
    private static final String SOME_CONNECTION = "some-connection";
    private static final String INGRESS_PATH = "/plugsy";
    private static final String ACTUATOR_HEALTH = "/actuator/health";
    private static final String ENTANDO_APP = "entando-app";
    private static final String SUPERUSER = "superuser";
    private static final String ADMIN = "admin";
    private static final String ADMINISTRATOR = "Administrator";
    private static final String PARAMETER_NAME = "env";
    private static final String PARAMETER_VALUE = "B";
    private static final String MY_KEYCLOAK = "my-keycloak";

    private static final String SNAPSHOT = "6.1.0-SNAPSHOT";
    private static final String ENTANDO_SOMEKEYCLOAK = "entando/somekeycloak";
    private static final String MY_PUBLIC_CLIENT = "my-public-client";

    private KubernetesClient kubernetesClient;

    @Inject
    public DummyBean(KubernetesClient kubernetesClient) {
        this.kubernetesClient = kubernetesClient;
    }

    public static void main(String[] args) {
        new DummyBean(new DefaultKubernetesClient()).onStartup(null);
    }

    public void onStartup(/*@Observes*/ StartupEvent event) {
        try {
            deleteAll(getClient().customResources(EntandoDatabaseService.class));
            deleteAll(getClient().customResources(EntandoKeycloakServer.class));
            deleteAll(getClient().customResources(EntandoApp.class));
            deleteAll(getClient().customResources(EntandoPlugin.class));
            deleteAll(getClient().customResources(EntandoAppPluginLink.class));
            testCreateEntandoDatabaseService();
            testCreateEntandoKeycloakServer();
            testCreateEntandoApp();
            testCreateEntandoPlugin();
            testCreateEntandoAppPluginLink();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            new Thread(() -> System.exit(0)).start();
        }
    }

    private <R extends EntandoCustomResource> void deleteAll(MixedOperation<R, KubernetesResourceList<R>, Resource<R>> op) {
        try {
            op.inNamespace(MY_NAMESPACE).delete();
        } catch (KubernetesClientException e) {
            e.printStackTrace();
        }
        try {
            op.inNamespace(MY_APP_NAMESPACE).delete();
        } catch (KubernetesClientException e) {
            e.printStackTrace();
        }
    }

    public void testCreateEntandoPlugin() {
        //Given
        EntandoPlugin externalDatabase = new EntandoPluginBuilder()
                .withNewMetadata().withName(MY_PLUGIN)
                .withNamespace(MY_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .withDbms(DbmsVendor.MYSQL)
                .withImage(IMAGE)
                .addNewConnectionConfigName(SOME_CONNECTION)
                .withReplicas(5)
                .withIngressPath(INGRESS_PATH)
                .withHealthCheckPath(ACTUATOR_HEALTH)
                .withIngressHostName(MYHOST_COM)
                .withTlsSecretName(MY_TLS_SECRET)
                .addNewPermission(ENTANDO_APP, SUPERUSER)
                .addNewRole(ADMIN, ADMINISTRATOR)
                .addToEnvironmentVariables(PARAMETER_NAME, PARAMETER_VALUE)
                .withSecurityLevel(STRICT)
                .withNewKeycloakToUse()
                .withNamespace(MY_KEYCLOAK_NAME_SPACE)
                .withName(MY_KEYCLOAK_NAME)
                .withRealm(MY_KEYCLOAK_REALM)
                .withPublicClientId(MY_PUBLIC_CLIENT)
                .endKeycloakToUse()
                .endSpec()
                .build();
        getClient().namespaces().createOrReplace(new NamespaceBuilder().withNewMetadata().withName(MY_NAMESPACE).endMetadata().build());
        getClient().customResources(EntandoPlugin.class).inNamespace(MY_NAMESPACE).create(externalDatabase);
        //When
        KubernetesResourceList<EntandoPlugin> list = getClient().customResources(EntandoPlugin.class)
                .inNamespace(MY_NAMESPACE).list();
        EntandoPlugin actual = list.getItems().get(0);
        //Then
        assertThat(actual.getSpec().getDbms().get(), is(DbmsVendor.MYSQL));
        assertThat(actual.getSpec().getImage(), is(IMAGE));
        assertThat(actual.getSpec().getKeycloakToUse().get().getName(), is(MY_KEYCLOAK_NAME));
        assertThat(actual.getSpec().getKeycloakToUse().get().getNamespace().get(), is(MY_KEYCLOAK_NAME_SPACE));
        assertThat(actual.getSpec().getKeycloakToUse().get().getRealm(), is(MY_KEYCLOAK_REALM));
        assertThat(actual.getSpec().getTlsSecretName().get(), is(MY_TLS_SECRET));
        assertThat(actual.getTlsSecretName().get(), is(MY_TLS_SECRET));
        assertThat(actual.getSpec().getIngressHostName().get(), is(MYHOST_COM));
        assertThat(actual.getIngressHostName().get(), is(MYHOST_COM));
        assertThat(actual.getSpec().getConnectionConfigNames(), is(Arrays.asList(SOME_CONNECTION)));
        assertThat(actual.getSpec().getPermissions().get(0).getClientId(), is(ENTANDO_APP));
        assertThat(actual.getSpec().getPermissions().get(0).getRole(), is(SUPERUSER));
        assertThat(actual.getSpec().getRoles().get(0).getCode(), is(ADMIN));
        assertThat(actual.getSpec().getRoles().get(0).getName(), is(ADMINISTRATOR));
        assertThat(actual.getSpec().getSecurityLevel().get(), is(STRICT));
        assertThat(actual.getSpec().getIngressPath(), is(INGRESS_PATH));
        assertThat(actual.getSpec().getHealthCheckPath(), is(ACTUATOR_HEALTH));
        assertThat(actual.getSpec().getReplicas().get(), is(5));
        assertThat(actual.getMetadata().getName(), is(MY_PLUGIN));
        assertThat(actual.getStatus() != null, is(true));
    }

    public void testCreateEntandoApp() {
        //Given
        EntandoApp entandoApp = new EntandoAppBuilder()
                .withNewMetadata().withName(MY_APP)
                .withNamespace(MY_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .withDbms(DbmsVendor.MYSQL)
                .withCustomServerImage(MY_CUSTOM_SERVER_IMAGE)
                .withStandardServerImage(JeeServer.WILDFLY)
                .withReplicas(5)
                .withTlsSecretName(MY_TLS_SECRET)
                .withIngressHostName(MYINGRESS_COM)
                .withNewKeycloakToUse()
                .withNamespace(MY_KEYCLOAK_NAME_SPACE)
                .withName(MY_KEYCLOAK_NAME)
                .withRealm(MY_KEYCLOAK_REALM)
                .withPublicClientId(MY_PUBLIC_CLIENT)
                .endKeycloakToUse()
                .endSpec()
                .build();
        getClient().namespaces().createOrReplace(new NamespaceBuilder().withNewMetadata().withName(MY_NAMESPACE).endMetadata().build());
        getClient().customResources(EntandoApp.class).inNamespace(MY_NAMESPACE).create(entandoApp);
        //When
        KubernetesResourceList<EntandoApp> list = getClient().customResources(EntandoApp.class).inNamespace(MY_NAMESPACE)
                .list();
        EntandoApp actual = list.getItems().get(0);
        //Then
        assertThat(actual.getSpec().getDbms().get(), is(DbmsVendor.MYSQL));
        assertThat(actual.getSpec().getIngressHostName().get(), is(MYINGRESS_COM));
        assertThat(actual.getSpec().getKeycloakToUse().get().getName(), is(MY_KEYCLOAK_NAME));
        assertThat(actual.getSpec().getKeycloakToUse().get().getNamespace().get(), is(MY_KEYCLOAK_NAME_SPACE));
        assertThat(actual.getSpec().getKeycloakToUse().get().getRealm(), is(MY_KEYCLOAK_REALM));
        assertThat(actual.getTlsSecretName().get(), is(MY_TLS_SECRET));
        assertThat(actual.getIngressHostName().get(), is(MYINGRESS_COM));
        assertThat(actual.getSpec().getStandardServerImage().get(), is(JeeServer.WILDFLY));
        assertThat(actual.getSpec().getReplicas().get(), is(5));
        assertThat(actual.getSpec().getTlsSecretName().get(), is(MY_TLS_SECRET));
        assertThat(actual.getSpec().getCustomServerImage().isPresent(), is(false));//because it was overridden by a standard image
        assertThat(actual.getMetadata().getName(), is(MY_APP));

        //Test statuses
        ServerStatus db = new ServerStatus("db");
        db.setEntandoControllerFailure(
                new EntandoControllerFailureBuilder().withFailedObjectKind("app").withFailedObjectName(MY_APP).withMessage("Failed")
                        .withDetailMessage("Failedmiserably").build());
        actual = getClient().customResources(EntandoApp.class).inNamespace(MY_NAMESPACE).withName(MY_APP).fromServer()
                .get();
        ServerStatus db1 = actual.getStatus().getServerStatus("db").get();
        assertThat(db1.getEntandoControllerFailure().get().getMessage(), "Failed");
        assertThat(db1.getEntandoControllerFailure().get().getDetailMessage(), "Failedmiserably");
        assertThat(actual.getStatus().getPhase(), is(EntandoDeploymentPhase.STARTED));
    }

    public void testCreateEntandoKeycloakServer() {
        //Given
        EntandoKeycloakServer keycloakServer = new EntandoKeycloakServerBuilder()
                .withNewMetadata().withName(MY_KEYCLOAK)
                .withNamespace(MY_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .withDbms(DbmsVendor.MYSQL)
                .withCustomImage(ENTANDO_SOMEKEYCLOAK)
                .withReplicas(5)
                .withDefault(true)
                .withIngressHostName(MYHOST_COM)
                .withTlsSecretName(MY_TLS_SECRET)
                .endSpec()
                .build();
        getClient().namespaces().createOrReplace(new NamespaceBuilder().withNewMetadata().withName(MY_NAMESPACE).endMetadata().build());
        getClient().customResources(EntandoKeycloakServer.class).inNamespace(MY_NAMESPACE)
                .create(keycloakServer);
        //When
        KubernetesResourceList<EntandoKeycloakServer> list = getClient().customResources(EntandoKeycloakServer.class)
                .inNamespace(MY_NAMESPACE).list();
        EntandoKeycloakServer actual = list.getItems().get(0);
        //Then
        assertThat(actual.getSpec().getDbms().get(), is(DbmsVendor.MYSQL));
        assertThat(actual.getSpec().getCustomImage().get(), is(ENTANDO_SOMEKEYCLOAK));
        assertThat(actual.getSpec().getIngressHostName().get(), is(MYHOST_COM));
        assertThat(actual.getSpec().getReplicas().get(), is(5));
        assertThat(actual.getSpec().getTlsSecretName().get(), is(MY_TLS_SECRET));
        assertThat(actual.getSpec().isDefault(), is(true));
        assertThat(actual.getMetadata().getName(), is(MY_KEYCLOAK));
    }

    public void testCreateEntandoAppPluginLink() {
        //Given
        EntandoAppPluginLink entandoAppPluginLink = new EntandoAppPluginLinkBuilder()
                .withNewMetadata().withName(MY_PLUGIN)
                .withNamespace(MY_APP_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .withEntandoApp(MY_APP_NAMESPACE, MY_APP)
                .withEntandoPlugin(MY_PLUGIN_NAMEPSACE, MY_PLUGIN)
                .endSpec()
                .build();
        getClient().namespaces().createOrReplace(new NamespaceBuilder().withNewMetadata().withName(MY_NAMESPACE).endMetadata().build());
        getClient().customResources(EntandoAppPluginLink.class).inNamespace(MY_APP_NAMESPACE)
                .create(entandoAppPluginLink);
        //When
        KubernetesResourceList<EntandoAppPluginLink> list = getClient().customResources(EntandoAppPluginLink.class)
                .inNamespace(MY_APP_NAMESPACE).list();
        EntandoAppPluginLink actual = list.getItems().get(0);
        //Then
        assertThat(actual.getSpec().getEntandoAppName(), is(MY_APP));
        assertThat(actual.getSpec().getEntandoAppNamespace(), is(MY_APP_NAMESPACE));
        assertThat(actual.getSpec().getEntandoPluginName(), is(MY_PLUGIN));
        assertThat(actual.getSpec().getEntandoPluginNamespace(), is(MY_PLUGIN_NAMEPSACE));
        assertThat(actual.getMetadata().getName(), is(MY_PLUGIN));
    }

    public void testCreateEntandoDatabaseService() {
        //Given
        EntandoDatabaseService externalDatabase = new EntandoDatabaseServiceBuilder()
                .withNewMetadata().withName(MY_EXTERNAL_DATABASE)
                .withNamespace(MY_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .withDatabaseName(MY_DB)
                .withHost(MYHOST_COM)
                .withPort(PORT_1521)
                .withSecretName(MY_DB_SECRET)
                .withDbms(DbmsVendor.ORACLE)
                .endSpec()
                .build();
        getClient().namespaces().createOrReplace(new NamespaceBuilder().withNewMetadata().withName(MY_NAMESPACE).endMetadata().build());
        getClient().customResources(EntandoDatabaseService.class).inNamespace(MY_NAMESPACE)
                .create(externalDatabase);
        //When
        KubernetesResourceList<EntandoDatabaseService> list = getClient().customResources(EntandoDatabaseService.class)
                .inNamespace(MY_NAMESPACE).list();
        EntandoDatabaseService actual = list.getItems().get(0);
        //Then
        assertThat(actual.getSpec().getDatabaseName(), is(MY_DB));
        assertThat(actual.getSpec().getHost(), is(MYHOST_COM));
        assertThat(actual.getSpec().getPort().get(), is(PORT_1521));
        assertThat(actual.getSpec().getDbms(), is(DbmsVendor.ORACLE));
        assertThat(actual.getSpec().getSecretName(), is(MY_DB_SECRET));
        assertThat(actual.getMetadata().getName(), is(MY_EXTERNAL_DATABASE));
    }

    public KubernetesClient getClient() {
        return kubernetesClient;
    }

    private void assertThat(Object actual, Object expected) {
        if (!Objects.equals(actual, expected)) {
            throw new AssertionError();
        }
    }

    private Object is(Object expected) {
        return expected;
    }
}
