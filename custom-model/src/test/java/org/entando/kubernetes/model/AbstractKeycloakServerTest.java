package org.entando.kubernetes.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.internal.CustomResourceOperationsImpl;
import java.util.List;
import org.entando.kubernetes.model.keycloakserver.DoneableKeycloakServer;
import org.entando.kubernetes.model.keycloakserver.KeycloakServer;
import org.entando.kubernetes.model.keycloakserver.KeycloakServerBuilder;
import org.entando.kubernetes.model.keycloakserver.KeycloakServerList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class AbstractKeycloakServerTest {

    protected static final String MY_NAMESPACE = "my-namespace";
    protected static final String MY_KEYCLOAK = "my-keycloak";
    private static final String SNAPSHOT = "6.1.0-SNAPSHOT";
    private static final String ENTANDO_SOMEKEYCLOAK = "entando/somekeycloak";
    private static final String MYHOST_COM = "myhost.com";
    private static CustomResourceDefinition keycloakServerCrd;

    private static CustomResourceOperationsImpl<KeycloakServer, KeycloakServerList,
            DoneableKeycloakServer> produceAllKeycloakServers(
            KubernetesClient client) {
        synchronized (AbstractKeycloakServerTest.class) {
            keycloakServerCrd = client.customResourceDefinitions().withName(KeycloakServer.CRD_NAME).get();
            if (keycloakServerCrd == null) {
                List<HasMetadata> list = client.load(Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("crd/EntandoKeycloakServerCRD.yaml")).get();
                keycloakServerCrd = (CustomResourceDefinition) list.get(0);
                // see issue https://github.com/fabric8io/kubernetes-client/issues/1486
                keycloakServerCrd.getSpec().getValidation().getOpenAPIV3Schema().setDependencies(null);
                client.customResourceDefinitions().create(keycloakServerCrd);
            }

        }
        return (CustomResourceOperationsImpl<KeycloakServer, KeycloakServerList, DoneableKeycloakServer>) client
                .customResources(keycloakServerCrd, KeycloakServer.class, KeycloakServerList.class, DoneableKeycloakServer.class);
    }

    @BeforeEach
    public void deleteKeycloakServer() throws InterruptedException {
        keycloakServers().inNamespace(MY_NAMESPACE).withName(MY_KEYCLOAK).delete();
        while (keycloakServers().inNamespace(MY_NAMESPACE).list().getItems().size() > 0) {
            Thread.sleep(100);
        }
    }

    @Test
    public void testCreateKeycloakServer() {
        //Given
        KeycloakServer externalDatabase = new KeycloakServerBuilder()
                .withNewMetadata().withName(MY_KEYCLOAK)
                .withNamespace(MY_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .withDbms(DbmsImageVendor.MYSQL)
                .withEntandoImageVersion(SNAPSHOT)
                .withImageName(ENTANDO_SOMEKEYCLOAK)
                .withReplicas(5)
                .withIngressHostName(MYHOST_COM)
                .withTlsEnabled(true)
                .endSpec()
                .build();
        getClient().namespaces().createOrReplaceWithNew().withNewMetadata().withName(MY_NAMESPACE).endMetadata().done();
        keycloakServers().inNamespace(MY_NAMESPACE).create(externalDatabase);
        //When
        KeycloakServerList list = keycloakServers().inNamespace(MY_NAMESPACE).list();
        KeycloakServer actual = list.getItems().get(0);
        //Then
        assertThat(actual.getSpec().getDbms().get(), is(DbmsImageVendor.MYSQL));
        assertThat(actual.getSpec().getEntandoImageVersion().get(), is(SNAPSHOT));
        assertThat(actual.getSpec().getImageName().get(), is(ENTANDO_SOMEKEYCLOAK));
        assertThat(actual.getSpec().getIngressHostName().get(), is(MYHOST_COM));
        assertThat(actual.getSpec().getReplicas(), is(5));
        assertThat(actual.getSpec().getTlsEnabled().get(), is(true));
        assertThat(actual.getMetadata().getName(), is(MY_KEYCLOAK));
    }

    protected abstract KubernetesClient getClient();

    @Test
    public void testEditKeycloakServer() {
        //Given
        KeycloakServer keycloakServer = new KeycloakServerBuilder()
                .withNewMetadata()
                .withName(MY_KEYCLOAK)
                .withNamespace(MY_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .withDbms(DbmsImageVendor.POSTGRESQL)
                .withEntandoImageVersion("6.2.0-SNAPSHOT")
                .withIngressHostName(MYHOST_COM)
                .withImageName("entando/anotherkeycloak")
                .withReplicas(3)
                .withTlsEnabled(false)
                .endSpec()
                .build();
        getClient().namespaces().createOrReplaceWithNew().withNewMetadata().withName(MY_NAMESPACE).endMetadata().done();

        //When
        //We are not using the mock server here because of a known bug
        KeycloakServer actual = editKeycloakServer(keycloakServer)
                .editMetadata().addToLabels("my-label", "my-value")
                .endMetadata()
                .editSpec()
                .withDbms(DbmsImageVendor.MYSQL)
                .withEntandoImageVersion(SNAPSHOT)
                .withImageName(ENTANDO_SOMEKEYCLOAK)
                .withIngressHostName(MYHOST_COM)
                .withReplicas(5)
                .withTlsEnabled(true)
                .endSpec()
                .withStatus(new WebServerStatus("some-qualifier"))
                .withStatus(new WebServerStatus("some-other-qualifier"))
                .withStatus(new WebServerStatus("some-qualifier"))
                .withStatus(new DbServerStatus("another-qualifier"))
                .withPhase(EntandoDeploymentPhase.STARTED)
                .done();
        //Then
        assertThat(actual.getSpec().getDbms().get(), is(DbmsImageVendor.MYSQL));
        assertThat(actual.getSpec().getEntandoImageVersion().get(), is(SNAPSHOT));
        assertThat(actual.getSpec().getImageName().get(), is(ENTANDO_SOMEKEYCLOAK));
        assertThat(actual.getSpec().getIngressHostName().get(), is(MYHOST_COM));
        assertThat(actual.getSpec().getReplicas(), is(5));
        assertThat(actual.getSpec().getTlsEnabled().get(), is(true));
        assertThat(actual.getMetadata().getName(), is(MY_KEYCLOAK));
        assertThat("the status reflects", actual.getStatus().forServerQualifiedBy("some-qualifier").isPresent());
        assertThat("the status reflects", actual.getStatus().forServerQualifiedBy("some-other-qualifier").isPresent());
        assertThat("the status reflects", actual.getStatus().forDbQualifiedBy("another-qualifier").isPresent());
    }

    protected abstract DoneableKeycloakServer editKeycloakServer(KeycloakServer keycloakServer);

    protected CustomResourceOperationsImpl<KeycloakServer, KeycloakServerList, DoneableKeycloakServer> keycloakServers() {
        return produceAllKeycloakServers(
                getClient());
    }
}
