package org.entando.kubernetes.model;

import static org.entando.kubernetes.model.plugin.PluginSecurityLevel.STRICT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.internal.CustomResourceOperationsImpl;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.entando.kubernetes.model.inprocesstest.EntandoPluginMockedTest;
import org.entando.kubernetes.model.plugin.DoneableEntandoPlugin;
import org.entando.kubernetes.model.plugin.EntandoPlugin;
import org.entando.kubernetes.model.plugin.EntandoPluginBuilder;
import org.entando.kubernetes.model.plugin.EntandoPluginList;
import org.entando.kubernetes.model.plugin.ExpectedRole;
import org.entando.kubernetes.model.plugin.Permission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class AbstractEntandoPluginTest {

    protected static final String MY_NAMESPACE = "my-namespace";
    protected static final String MY_PLUGIN = "my-plugin";
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
    private static final String MYKEYCLOAKNAMESPACE = "mykeycloaknamespace";
    private static final String MY_KEYCLOAK = "my-keycloak";
    private static final String MY_APP = "my-app";
    private static CustomResourceDefinition entandoPluginCrd;

    private static CustomResourceOperationsImpl<EntandoPlugin, EntandoPluginList,
            DoneableEntandoPlugin> produceAllEntandoPlugins(
            KubernetesClient client) {
        synchronized (EntandoPluginMockedTest.class) {
            entandoPluginCrd = client.customResourceDefinitions().withName(EntandoPlugin.CRD_NAME).get();
            if (entandoPluginCrd == null) {
                List<HasMetadata> list = client.load(Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("crd/EntandoPluginCRD.yaml")).get();
                entandoPluginCrd = (CustomResourceDefinition) list.get(0);
                // see issue https://github.com/fabric8io/kubernetes-client/issues/1486
                entandoPluginCrd.getSpec().getValidation().getOpenAPIV3Schema().setDependencies(null);
                client.customResourceDefinitions().create(entandoPluginCrd);
            }
        }
        return (CustomResourceOperationsImpl<EntandoPlugin, EntandoPluginList, DoneableEntandoPlugin>) client
                .customResources(entandoPluginCrd, EntandoPlugin.class, EntandoPluginList.class, DoneableEntandoPlugin.class);
    }

    @BeforeEach
    public void deleteEntandPlugin() throws InterruptedException {
        entandoPlugins().inNamespace(MY_NAMESPACE).withName(MY_PLUGIN).delete();
        while (entandoPlugins().inNamespace(MY_NAMESPACE).list().getItems().size() > 0) {
            Thread.sleep(100);
        }
    }

    @Test
    public void testCreateEntandoPlugin() {
        //Given
        EntandoPlugin externalDatabase = new EntandoPluginBuilder()
                .withNewMetadata().withName(MY_PLUGIN)
                .withNamespace(MY_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .withDbms(DbmsImageVendor.MYSQL)
                .withImage(IMAGE)
                .addNewConnectionConfigName(SOME_CONNECTION)
                .withReplicas(5)
                .withIngressPath(INGRESS_PATH)
                .withHealthCheckPath(ACTUATOR_HEALTH)
                .withPermission(ENTANDO_APP, SUPERUSER)
                .withRole(ADMIN, ADMINISTRATOR)
                .addNewParameter(PARAMETER_NAME, PARAMETER_VALUE)
                .withSecurityLevel(STRICT)
                .withEntandoApp(MY_NAMESPACE, MY_APP)
                .withKeycloakServer(MYKEYCLOAKNAMESPACE, MY_KEYCLOAK)
                .endSpec()
                .build();
        getClient().namespaces().createOrReplaceWithNew().withNewMetadata().withName(MY_NAMESPACE).endMetadata().done();
        entandoPlugins().inNamespace(MY_NAMESPACE).create(externalDatabase);
        //When
        EntandoPluginList list = entandoPlugins().inNamespace(MY_NAMESPACE).list();
        EntandoPlugin actual = list.getItems().get(0);
        //Then
        assertThat(actual.getSpec().getDbms().get(), is(DbmsImageVendor.MYSQL));
        assertThat(actual.getSpec().getImage(), is(IMAGE));
        assertThat(actual.getSpec().getKeycloakServerName(), is(MY_KEYCLOAK));
        assertThat(actual.getSpec().getKeycloakServerNamespace(), is(MYKEYCLOAKNAMESPACE));
        assertThat(actual.getKeycloakServerName(), is(MY_KEYCLOAK));
        assertThat(actual.getKeycloakServerNamespace(), is(MYKEYCLOAKNAMESPACE));
        assertThat(actual.getSpec().getEntandoAppName(), is(MY_APP));
        assertThat(actual.getSpec().getEntandoAppNamespace(), is(MY_NAMESPACE));
        assertThat(actual.getSpec().getConnectionConfigNames(), is(Arrays.asList(SOME_CONNECTION)));
        assertThat(actual.getSpec().getPermissions().get(0).getClientId(), is(ENTANDO_APP));
        assertThat(actual.getSpec().getPermissions().get(0).getRole(), is(SUPERUSER));
        assertThat(actual.getSpec().getParameters().get(PARAMETER_NAME), is(PARAMETER_VALUE));
        assertThat(actual.getSpec().getRoles().get(0).getCode(), is(ADMIN));
        assertThat(actual.getSpec().getRoles().get(0).getName(), is(ADMINISTRATOR));
        assertThat(actual.getSpec().getSecurityLevel().get(), is(STRICT));
        assertThat(actual.getSpec().getIngressPath(), is(INGRESS_PATH));
        assertThat(actual.getSpec().getHealthCheckPath(), is(ACTUATOR_HEALTH));
        assertThat(actual.getSpec().getReplicas(), is(5));
        assertThat(actual.getMetadata().getName(), is(MY_PLUGIN));
    }

    @Test
    public void testEditEntandoPlugin() {
        //Given
        EntandoPlugin entandoPlugin = new EntandoPluginBuilder()
                .withNewMetadata()
                .withName(MY_PLUGIN)
                .withNamespace(MY_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .withDbms(DbmsImageVendor.POSTGRESQL)
                .withImage("entando/enoatherplugin:1.0.2")
                .addNewConnectionConfigName("another-connection")
                .withReplicas(5)
                .withIngressPath(INGRESS_PATH)
                .withHealthCheckPath("/actuator/unhealth")
                .withPermission("entando-usermgment", "subuser")
                .withRole("user", "User")
                .addNewParameter(PARAMETER_NAME, "A")
                .withSecurityLevel(STRICT)
                .withEntandoApp("somenamespace", "another-app")
                .withKeycloakServer("somekeycloaknamespace", "another-keycloak")
                .endSpec()
                .build();
        getClient().namespaces().createOrReplaceWithNew().withNewMetadata().withName(MY_NAMESPACE).endMetadata().done();
        //When
        //We are not using the mock server here because of a known bug
        EntandoPlugin actual = editEntandoPlugin(entandoPlugin)
                .editMetadata().addToLabels("my-label", "my-value")
                .endMetadata()
                .editSpec()
                .withDbms(DbmsImageVendor.MYSQL)
                .withImage(IMAGE)
                .withConnectionConfigNames(Arrays.asList(SOME_CONNECTION))
                .withReplicas(5)
                .withHealthCheckPath(ACTUATOR_HEALTH)
                .withPermissions(Arrays.asList(new Permission(ENTANDO_APP, SUPERUSER)))
                .withRoles(Arrays.asList(new ExpectedRole(ADMIN, ADMINISTRATOR)))
                .withParameters(Collections.singletonMap(PARAMETER_NAME, PARAMETER_VALUE))
                .withSecurityLevel(STRICT)
                .withEntandoApp(MY_NAMESPACE, MY_APP)
                .withKeycloakServer(MYKEYCLOAKNAMESPACE, MY_KEYCLOAK)
                .endSpec()
                .withStatus(new WebServerStatus("some-qualifier"))
                .withStatus(new DbServerStatus("another-qualifier"))
                .withPhase(EntandoDeploymentPhase.STARTED)
                .done();
        //Then
        assertThat(actual.getSpec().getDbms().get(), is(DbmsImageVendor.MYSQL));
        assertThat(actual.getSpec().getImage(), is(IMAGE));
        assertThat(actual.getSpec().getKeycloakServerName(), is(MY_KEYCLOAK));
        assertThat(actual.getSpec().getKeycloakServerNamespace(), is(MYKEYCLOAKNAMESPACE));
        assertThat(actual.getKeycloakServerName(), is(MY_KEYCLOAK));
        assertThat(actual.getKeycloakServerNamespace(), is(MYKEYCLOAKNAMESPACE));
        assertThat(actual.getSpec().getEntandoAppName(), is(MY_APP));
        assertThat(actual.getSpec().getEntandoAppNamespace(), is(MY_NAMESPACE));
        assertThat(actual.getSpec().getConnectionConfigNames(), is(Arrays.asList(SOME_CONNECTION)));
        assertThat(actual.getSpec().getPermissions().get(0).getClientId(), is(ENTANDO_APP));
        assertThat(actual.getSpec().getPermissions().get(0).getRole(), is(SUPERUSER));
        assertThat(actual.getSpec().getRoles().get(0).getCode(), is(ADMIN));
        assertThat(actual.getSpec().getRoles().get(0).getName(), is(ADMINISTRATOR));
        assertThat(actual.getSpec().getSecurityLevel().get(), is(STRICT));
        assertThat(actual.getSpec().getParameters().get(PARAMETER_NAME), is(PARAMETER_VALUE));
        assertThat(actual.getSpec().getReplicas(), is(5));
        assertThat(actual.getMetadata().getName(), is(MY_PLUGIN));
    }

    protected abstract DoneableEntandoPlugin editEntandoPlugin(EntandoPlugin entandoPlugin);

    protected CustomResourceOperationsImpl<EntandoPlugin, EntandoPluginList, DoneableEntandoPlugin> entandoPlugins() {
        return produceAllEntandoPlugins(
                getClient());
    }

    protected abstract KubernetesClient getClient();
}
