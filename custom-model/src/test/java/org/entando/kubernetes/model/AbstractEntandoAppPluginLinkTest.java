package org.entando.kubernetes.model;

import static org.entando.kubernetes.model.link.EntandoAppPluginLinkOperationFactory.produceAllEntandoAppPluginLinks;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import io.fabric8.kubernetes.client.dsl.internal.CustomResourceOperationsImpl;
import org.entando.kubernetes.model.link.DoneableEntandoAppPluginLink;
import org.entando.kubernetes.model.link.EntandoAppPluginLink;
import org.entando.kubernetes.model.link.EntandoAppPluginLinkBuilder;
import org.entando.kubernetes.model.link.EntandoAppPluginLinkList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class AbstractEntandoAppPluginLinkTest implements CustomResourceTestUtil {

    public static final String MY_APP_NAMESPACE = "my-app-namespace";
    public static final String MY_PLUGIN_NAMEPSACE = "my-plugin-namepsace";
    protected static final String MY_PLUGIN = "my-plugin";
    private static final String MY_APP = "my-app";

    @BeforeEach
    public void deleteEntandoAppPluginLinks() throws InterruptedException {
        prepareNamespace(entandoAppPluginLinks(), MY_APP_NAMESPACE);
    }

    @Test
    public void testCreateEntandoAppPluginLink() throws InterruptedException {
        //Given
        EntandoAppPluginLink externalDatabase = new EntandoAppPluginLinkBuilder()
                .withNewMetadata().withName(MY_PLUGIN)
                .withNamespace(MY_APP_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .withEntandoApp(MY_APP_NAMESPACE, MY_APP)
                .withEntandoPlugin(MY_PLUGIN_NAMEPSACE, MY_PLUGIN)
                .endSpec()
                .build();
        getClient().namespaces().createOrReplaceWithNew().withNewMetadata().withName(MY_APP_NAMESPACE).endMetadata().done();
        entandoAppPluginLinks().inNamespace(MY_APP_NAMESPACE).create(externalDatabase);
        //When
        EntandoAppPluginLinkList list = entandoAppPluginLinks().inNamespace(MY_APP_NAMESPACE).list();
        EntandoAppPluginLink actual = list.getItems().get(0);
        //Then
        assertThat(actual.getSpec().getEntandoAppName(), is(MY_APP));
        assertThat(actual.getSpec().getEntandoAppNamespace(), is(MY_APP_NAMESPACE));
        assertThat(actual.getSpec().getEntandoPluginName(), is(MY_PLUGIN));
        assertThat(actual.getSpec().getEntandoPluginNamespace(), is(MY_PLUGIN_NAMEPSACE));
        assertThat(actual.getMetadata().getName(), is(MY_PLUGIN));
    }

    @Test
    public void testEditEntandoAppPluginLink() throws InterruptedException {
        //Given
        EntandoAppPluginLink entandoAppPluginLink = new EntandoAppPluginLinkBuilder()
                .withNewMetadata()
                .withName(MY_PLUGIN)
                .withNamespace(MY_APP_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .withEntandoApp("some-namespace", "some-app")
                .withEntandoPlugin("antoher-namespace", "some-plugin")
                .endSpec()
                .build();
        getClient().namespaces().createOrReplaceWithNew().withNewMetadata().withName(MY_APP_NAMESPACE).endMetadata().done();
        //When
        //We are not using the mock server here because of a known bug
        EntandoAppPluginLink actual = editEntandoAppPluginLink(entandoAppPluginLink)
                .editMetadata().addToLabels("my-label", "my-value")
                .endMetadata()
                .editSpec()
                .withEntandoApp(MY_APP_NAMESPACE, MY_APP)
                .withEntandoPlugin(MY_PLUGIN_NAMEPSACE, MY_PLUGIN)
                .endSpec()
                .withStatus(new WebServerStatus("some-qualifier"))
                .withStatus(new DbServerStatus("another-qualifier"))
                .withPhase(EntandoDeploymentPhase.STARTED)
                .done();
        //Then
        assertThat(actual.getSpec().getEntandoAppName(), is(MY_APP));
        assertThat(actual.getSpec().getEntandoAppNamespace(), is(MY_APP_NAMESPACE));
        assertThat(actual.getSpec().getEntandoPluginName(), is(MY_PLUGIN));
        assertThat(actual.getSpec().getEntandoPluginNamespace(), is(MY_PLUGIN_NAMEPSACE));
        assertThat(actual.getMetadata().getName(), is(MY_PLUGIN));
    }

    protected abstract DoneableEntandoAppPluginLink editEntandoAppPluginLink(EntandoAppPluginLink entandoAppPluginLink)
            throws InterruptedException;

    protected CustomResourceOperationsImpl<EntandoAppPluginLink, EntandoAppPluginLinkList,
            DoneableEntandoAppPluginLink> entandoAppPluginLinks()
            throws InterruptedException {
        return produceAllEntandoAppPluginLinks(getClient());
    }

}
