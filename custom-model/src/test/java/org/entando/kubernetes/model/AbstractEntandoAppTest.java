package org.entando.kubernetes.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.internal.CustomResourceOperationsImpl;
import java.util.List;
import org.entando.kubernetes.model.app.DoneableEntandoApp;
import org.entando.kubernetes.model.app.EntandoApp;
import org.entando.kubernetes.model.app.EntandoAppBuilder;
import org.entando.kubernetes.model.app.EntandoAppList;
import org.entando.kubernetes.model.inprocesstest.EntandoAppMockedTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class AbstractEntandoAppTest {

    protected static final String MY_NAMESPACE = "my-namespace";
    protected static final String MY_APP = "my-app";
    private static final String ENTANDO_IMAGE_VERSION = "6.1.0-SNAPSHOT";
    private static final String MYINGRESS_COM = "myingress.com";
    private static final String MYKEYCLOAKNAMESPACE = "mykeycloakn123-amespace-asdf";
    private static final String MY_KEYCLOAK = "my-keycloak";
    private static final String MY_VALUE = "my-value";
    private static final String MY_LABEL = "my-label";
    private static CustomResourceDefinition entandoAppCrd;

    private static CustomResourceOperationsImpl<EntandoApp, EntandoAppList,
            DoneableEntandoApp> produceAllEntandoApps(
            KubernetesClient client) {
        synchronized (EntandoAppMockedTest.class) {
            entandoAppCrd = client.customResourceDefinitions().withName(EntandoApp.CRD_NAME).get();
            if (entandoAppCrd == null) {
                List<HasMetadata> list = client.load(Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("crd/EntandoAppCRD.yaml")).get();
                entandoAppCrd = (CustomResourceDefinition) list.get(0);
                // see issue https://github.com/fabric8io/kubernetes-client/issues/1486
                entandoAppCrd.getSpec().getValidation().getOpenAPIV3Schema().setDependencies(null);
                client.customResourceDefinitions().create(entandoAppCrd);
            }

        }
        return (CustomResourceOperationsImpl<EntandoApp, EntandoAppList, DoneableEntandoApp>) client
                .customResources(entandoAppCrd, EntandoApp.class, EntandoAppList.class, DoneableEntandoApp.class);
    }

    @BeforeEach
    public void deleteExternalDatabase() throws InterruptedException {
        entandoApps().inNamespace(MY_NAMESPACE).withName(MY_APP).delete();
        while (entandoApps().inNamespace(MY_NAMESPACE).list().getItems().size() > 0) {
            Thread.sleep(100);
        }
    }

    @Test
    public void testCreateEntandoApp() {
        //Given
        EntandoApp externalDatabase = new EntandoAppBuilder()
                .withNewMetadata().withName(MY_APP)
                .withNamespace(MY_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .withDbms(DbmsImageVendor.MYSQL)
                .withEntandoImageVersion(ENTANDO_IMAGE_VERSION)
                .withJeeServer(JeeServer.WILDFLY)
                .withReplicas(5)
                .withTlsEnabled(true)
                .withIngressHostName(MYINGRESS_COM)
                .withKeycloakServer(MYKEYCLOAKNAMESPACE, MY_KEYCLOAK)
                .endSpec()
                .build();
        getClient().namespaces().createOrReplaceWithNew().withNewMetadata().withName(MY_NAMESPACE).endMetadata().done();
        entandoApps().inNamespace(MY_NAMESPACE).create(externalDatabase);
        //When
        EntandoAppList list = entandoApps().inNamespace(MY_NAMESPACE).list();
        EntandoApp actual = list.getItems().get(0);
        //Then
        assertThat(actual.getSpec().getDbms().get(), is(DbmsImageVendor.MYSQL));
        assertThat(actual.getSpec().getEntandoImageVersion().get(), is(ENTANDO_IMAGE_VERSION));
        assertThat(actual.getSpec().getIngressHostName().get(), is(MYINGRESS_COM));
        assertThat(actual.getSpec().getKeycloakServerName(), is(MY_KEYCLOAK));
        assertThat(actual.getSpec().getKeycloakServerNamespace(), is(MYKEYCLOAKNAMESPACE));
        assertThat(actual.getTlsEnabled().get(), is(true));
        assertThat(actual.getIngressHostName().get(), is(MYINGRESS_COM));
        assertThat(actual.getKeycloakServerName(), is(MY_KEYCLOAK));
        assertThat(actual.getKeycloakServerNamespace(), is(MYKEYCLOAKNAMESPACE));
        assertThat(actual.getSpec().getJeeServer().get(), is(JeeServer.WILDFLY));
        assertThat(actual.getSpec().getReplicas().get(), is(5));
        assertThat(actual.getSpec().getTlsEnabled().get(), is(true));
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
                .withDbms(DbmsImageVendor.POSTGRESQL)
                .withEntandoImageVersion("6.2.0-SNAPSHOT")
                .withJeeServer(JeeServer.WILDFLY)
                .withReplicas(4)
                .withTlsEnabled(false)
                .withIngressHostName("anotheringress.com")
                .withKeycloakServer("anotherkeycloaknamespace", "another-keycloak")
                .endSpec()
                .build();
        getClient().namespaces().createOrReplaceWithNew().withNewMetadata().withName(MY_NAMESPACE).endMetadata().done();
        //When
        //We are not using the mock server here because of a known bug
        EntandoApp actual = editEntandoApp(entandoApp)
                .editMetadata().addToLabels(MY_LABEL, MY_VALUE)
                .endMetadata()
                .editSpec()
                .withDbms(DbmsImageVendor.MYSQL)
                .withEntandoImageVersion(ENTANDO_IMAGE_VERSION)
                .withJeeServer(JeeServer.WILDFLY)
                .withReplicas(5)
                .withTlsEnabled(true)
                .withIngressHostName(MYINGRESS_COM)
                .withKeycloakServer(MYKEYCLOAKNAMESPACE, MY_KEYCLOAK)
                .endSpec()
                .withStatus(new WebServerStatus("some-qualifier"))
                .withStatus(new DbServerStatus("another-qualifier"))
                .withPhase(EntandoDeploymentPhase.STARTED)
                .done();
        //Then
        assertThat(actual.getSpec().getDbms().get(), is(DbmsImageVendor.MYSQL));
        assertThat(actual.getSpec().getEntandoImageVersion().get(), is(ENTANDO_IMAGE_VERSION));
        assertThat(actual.getSpec().getIngressHostName().get(), is(MYINGRESS_COM));
        assertThat(actual.getSpec().getKeycloakServerName(), is(MY_KEYCLOAK));
        assertThat(actual.getSpec().getKeycloakServerNamespace(), is(MYKEYCLOAKNAMESPACE));
        assertThat(actual.getSpec().getJeeServer().get(), is(JeeServer.WILDFLY));
        assertThat(actual.getSpec().getReplicas().get(), is(5));
        assertThat(actual.getSpec().getTlsEnabled().get(), is(true));
        assertThat(actual.getMetadata().getLabels().get(MY_LABEL), is(MY_VALUE));
    }

    protected abstract DoneableEntandoApp editEntandoApp(EntandoApp entandoApp);

    protected CustomResourceOperationsImpl<EntandoApp, EntandoAppList, DoneableEntandoApp> entandoApps() {
        return produceAllEntandoApps(
                getClient());
    }

    protected abstract KubernetesClient getClient();
}
