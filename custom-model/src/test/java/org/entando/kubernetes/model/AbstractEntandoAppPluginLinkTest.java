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

import static org.entando.kubernetes.model.link.EntandoAppPluginLinkOperationFactory.produceAllEntandoAppPluginLinks;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import io.fabric8.kubernetes.client.dsl.internal.CustomResourceOperationsImpl;
import org.entando.kubernetes.model.link.DoneableEntandoAppPluginLink;
import org.entando.kubernetes.model.link.EntandoAppPluginLink;
import org.entando.kubernetes.model.link.EntandoAppPluginLinkBuilder;
import org.entando.kubernetes.model.link.EntandoAppPluginLinkList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class AbstractEntandoAppPluginLinkTest implements CustomResourceTestUtil {

    protected static final String MY_PLUGIN = "my-plugin";
    private static final String MY_APP = "my-app";
    protected static final String MY_APP_NAMESPACE = TestConfig.calculateNameSpace("my-app-namespace");
    protected static  final String MY_PLUGIN_NAMESPACE = TestConfig.calculateNameSpace("my-plugin-namespace");

    @BeforeEach
    public void deleteEntandoAppPluginLinks() {
        prepareNamespace(entandoAppPluginLinks(), MY_APP_NAMESPACE);
    }

    @Test
    public void testCreateEntandoAppPluginLink() {
        //Given
        EntandoAppPluginLink entandoAppPluginLink = new EntandoAppPluginLinkBuilder()
                .withNewMetadata().withName(MY_PLUGIN)
                .withNamespace(MY_APP_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .withEntandoApp(MY_APP_NAMESPACE, MY_APP)
                .withEntandoPlugin(MY_PLUGIN_NAMESPACE, MY_PLUGIN)
                .endSpec()
                .build();
        getClient().namespaces().createOrReplaceWithNew().withNewMetadata().withName(MY_APP_NAMESPACE).endMetadata().done();
        entandoAppPluginLinks().inNamespace(MY_APP_NAMESPACE).createNew().withMetadata(entandoAppPluginLink.getMetadata())
                .withSpec(entandoAppPluginLink.getSpec())
                .done();
        //When
        EntandoAppPluginLinkList list = entandoAppPluginLinks().inNamespace(MY_APP_NAMESPACE).list();
        EntandoAppPluginLink actual = list.getItems().get(0);
        //Then
        assertThat(actual.getSpec().getEntandoAppName(), is(MY_APP));
        assertThat(actual.getSpec().getEntandoAppNamespace(), is(MY_APP_NAMESPACE));
        assertThat(actual.getSpec().getEntandoPluginName(), is(MY_PLUGIN));
        assertThat(actual.getSpec().getEntandoPluginNamespace(), is(MY_PLUGIN_NAMESPACE));
        assertThat(actual.getMetadata().getName(), is(MY_PLUGIN));
    }

    @Test
    public void testEditEntandoAppPluginLink() {
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
                .withEntandoPlugin(MY_PLUGIN_NAMESPACE, MY_PLUGIN)
                .endSpec()
                .withStatus(new WebServerStatus("some-qualifier"))
                .withStatus(new DbServerStatus("another-qualifier"))
                .withPhase(EntandoDeploymentPhase.STARTED)
                .done();
        //Then
        assertThat(actual.getSpec().getEntandoAppName(), is(MY_APP));
        assertThat(actual.getSpec().getEntandoAppNamespace(), is(MY_APP_NAMESPACE));
        assertThat(actual.getSpec().getEntandoPluginName(), is(MY_PLUGIN));
        assertThat(actual.getSpec().getEntandoPluginNamespace(), is(MY_PLUGIN_NAMESPACE));
        assertThat(actual.getMetadata().getName(), is(MY_PLUGIN));
        assertThat(actual.getStatus(), is(notNullValue()));
    }

    protected abstract DoneableEntandoAppPluginLink editEntandoAppPluginLink(EntandoAppPluginLink entandoAppPluginLink);

    protected CustomResourceOperationsImpl<EntandoAppPluginLink, EntandoAppPluginLinkList,
            DoneableEntandoAppPluginLink> entandoAppPluginLinks() {
        return produceAllEntandoAppPluginLinks(getClient());
    }

}
