package org.entando.kubernetes.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import io.fabric8.kubernetes.client.dsl.internal.CustomResourceOperationsImpl;
import org.entando.kubernetes.model.infrastructure.DoneableEntandoClusterInfrastructure;
import org.entando.kubernetes.model.infrastructure.EntandoClusterInfrastructure;
import org.entando.kubernetes.model.infrastructure.EntandoClusterInfrastructureBuilder;
import org.entando.kubernetes.model.infrastructure.EntandoClusterInfrastructureList;
import org.entando.kubernetes.model.infrastructure.EntandoClusterInfrastructureOperationFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class AbstractEntandoClusterInfrastructureTest implements CustomResourceTestUtil {

    protected static final String MY_NAMESPACE = "my-namespace";
    protected static final String MY_ENTANDO_CLUSTER_INFRASTRUCTURE = "my-entando-cluster-infrastructure";

    private static final String SNAPSHOT = "6.1.0-SNAPSHOT";
    private static final String MYHOST_COM = "myhost.com";
    private static final String MY_TLS_SECRET = "my-tls-secret";
    private static final String MY_KEYCLOAK_SECRET = "my-keycloak-secret";

    @BeforeEach
    public void deleteEntandoClusterInfrastructure() throws InterruptedException {
        prepareNamespace(entandoInfrastructure(), MY_NAMESPACE);
    }

    @Test
    public void testCreateEntandoClusterInfrastructure() throws InterruptedException {
        //Given
        EntandoClusterInfrastructure externalDatabase = new EntandoClusterInfrastructureBuilder()
                .withNewMetadata().withName(MY_ENTANDO_CLUSTER_INFRASTRUCTURE)
                .withNamespace(MY_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .withDbms(DbmsImageVendor.MYSQL)
                .withEntandoImageVersion(SNAPSHOT)
                .withReplicas(5)
                .withIngressHostName(MYHOST_COM)
                .withTlsSecretName(MY_TLS_SECRET)
                .withKeycloakSecretToUse(MY_KEYCLOAK_SECRET)
                .withDefault(true)
                .endSpec()
                .build();
        getClient().namespaces().createOrReplaceWithNew().withNewMetadata().withName(MY_NAMESPACE).endMetadata().done();
        entandoInfrastructure().inNamespace(MY_NAMESPACE).create(externalDatabase);
        //When
        EntandoClusterInfrastructureList list = entandoInfrastructure().inNamespace(MY_NAMESPACE).list();
        EntandoClusterInfrastructure actual = list.getItems().get(0);
        //Then
        assertThat(actual.getSpec().getDbms().get(), is(DbmsImageVendor.MYSQL));
        assertThat(actual.getSpec().getEntandoImageVersion().get(), is(SNAPSHOT));
        assertThat(actual.getSpec().getKeycloakSecretToUse().get(), is(MY_KEYCLOAK_SECRET));
        assertThat(actual.getKeycloakSecretToUse().get(), is(MY_KEYCLOAK_SECRET));
        assertThat(actual.getSpec().getIngressHostName().get(), is(MYHOST_COM));
        assertThat(actual.getSpec().getReplicas(), is(5));
        assertThat(actual.getSpec().getTlsSecretName().get(), is(MY_TLS_SECRET));
        assertThat(actual.getSpec().isDefault(), is(true));
        assertThat(actual.getTlsSecretName().get(), is(MY_TLS_SECRET));
        assertThat(actual.getMetadata().getName(), is(MY_ENTANDO_CLUSTER_INFRASTRUCTURE));
    }

    @Test
    public void testEditEntandoClusterInfrastructure() throws InterruptedException {
        //Given
        EntandoClusterInfrastructure keycloakServer = new EntandoClusterInfrastructureBuilder()
                .withNewMetadata()
                .withName(MY_ENTANDO_CLUSTER_INFRASTRUCTURE)
                .withNamespace(MY_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .withDbms(DbmsImageVendor.POSTGRESQL)
                .withEntandoImageVersion("6.2.0-SNAPSHOT")
                .withIngressHostName(MYHOST_COM)
                .withReplicas(3)
                .withKeycloakSecretToUse("some-othersecret")
                .withTlsSecretName("some-othersecret")
                .withDefault(false)
                .endSpec()
                .build();
        getClient().namespaces().createOrReplaceWithNew().withNewMetadata().withName(MY_NAMESPACE).endMetadata().done();

        //When
        //We are not using the mock server here because of a known bug
        EntandoClusterInfrastructure actual = editEntandoClusterInfrastructure(keycloakServer)
                .editMetadata().addToLabels("my-label", "my-value")
                .endMetadata()
                .editSpec()
                .withDbms(DbmsImageVendor.MYSQL)
                .withEntandoImageVersion(SNAPSHOT)
                .withIngressHostName(MYHOST_COM)
                .withReplicas(5)
                .withKeycloakSecretToUse(MY_KEYCLOAK_SECRET)
                .withTlsSecretName(MY_TLS_SECRET)
                .withDefault(true)
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
        assertThat(actual.getSpec().getKeycloakSecretToUse().get(), is(MY_KEYCLOAK_SECRET));
        assertThat(actual.getSpec().getIngressHostName().get(), is(MYHOST_COM));
        assertThat(actual.getSpec().getReplicas(), is(5));
        assertThat(actual.getSpec().getTlsSecretName().get(), is(MY_TLS_SECRET));
        assertThat(actual.getSpec().isDefault(), is(true));
        assertThat(actual.getMetadata().getName(), is(MY_ENTANDO_CLUSTER_INFRASTRUCTURE));
        assertThat("the status reflects", actual.getStatus().forServerQualifiedBy("some-qualifier").isPresent());
        assertThat("the status reflects", actual.getStatus().forServerQualifiedBy("some-other-qualifier").isPresent());
        assertThat("the status reflects", actual.getStatus().forDbQualifiedBy("another-qualifier").isPresent());
    }

    protected abstract DoneableEntandoClusterInfrastructure editEntandoClusterInfrastructure(EntandoClusterInfrastructure
            keycloakServer) throws InterruptedException;

    protected CustomResourceOperationsImpl<EntandoClusterInfrastructure, EntandoClusterInfrastructureList,
            DoneableEntandoClusterInfrastructure> entandoInfrastructure() throws InterruptedException {
        return EntandoClusterInfrastructureOperationFactory.produceAllEntandoClusterInfrastructures(getClient());
    }
}
