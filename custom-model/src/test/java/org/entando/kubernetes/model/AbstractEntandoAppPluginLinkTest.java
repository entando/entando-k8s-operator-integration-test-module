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
import static org.hamcrest.Matchers.notNullValue;

import org.entando.kubernetes.model.link.EntandoAppPluginLink;
import org.entando.kubernetes.model.link.EntandoAppPluginLinkBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//Sonar doesn't pick up that this class is extended in other packages
@SuppressWarnings("java:S5786")
public abstract class AbstractEntandoAppPluginLinkTest implements CustomResourceTestUtil {

    protected static final String MY_APP_PLUGIN_LINK = "my-app-plugin-link";
    protected static final String MY_PLUGIN = "my-plugin";
    protected static final String MY_APP_NAMESPACE = TestConfig.calculateNameSpace("my-app-namespace");
    protected static final String MY_PLUGIN_NAMESPACE = TestConfig.calculateNameSpace("my-plugin-namespace");
    private static final String MY_APP = "my-app";

    @BeforeEach
    public void deleteEntandoAppPluginLinks() {

        prepareNamespace(getClient().customResources(EntandoAppPluginLink.class), MY_APP_NAMESPACE);
    }

    @Test
    void testCreateEntandoAppPluginLink() {
        //Given
        EntandoAppPluginLink entandoAppPluginLink = new EntandoAppPluginLinkBuilder()
                .withNewMetadata().withName(MY_APP_PLUGIN_LINK)
                .withNamespace(MY_APP_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .withEntandoApp(MY_APP_NAMESPACE, MY_APP)
                .withEntandoPlugin(MY_PLUGIN_NAMESPACE, MY_PLUGIN)
                .endSpec()
                .build();

        getClient().customResources(EntandoAppPluginLink.class).inNamespace(MY_APP_NAMESPACE)
                .create(new EntandoAppPluginLinkBuilder().withMetadata(entandoAppPluginLink.getMetadata())
                        .withSpec(entandoAppPluginLink.getSpec())
                        .build());
        //When

        EntandoAppPluginLink actual = getClient().customResources(EntandoAppPluginLink.class).inNamespace(MY_APP_NAMESPACE)
                .withName(MY_APP_PLUGIN_LINK).get();
        //Then
        assertThat(actual.getSpec().getEntandoAppName(), is(MY_APP));
        assertThat(actual.getSpec().getEntandoAppNamespace().get(), is(MY_APP_NAMESPACE));
        assertThat(actual.getSpec().getEntandoPluginName(), is(MY_PLUGIN));
        assertThat(actual.getSpec().getEntandoPluginNamespace().get(), is(MY_PLUGIN_NAMESPACE));
        assertThat(actual.getMetadata().getName(), is(MY_APP_PLUGIN_LINK));
    }

    @Test
    void testEditEntandoAppPluginLink() {
        //Given
        EntandoAppPluginLink entandoAppPluginLink = new EntandoAppPluginLinkBuilder()
                .withNewMetadata()
                .withName(MY_APP_PLUGIN_LINK)
                .withNamespace(MY_APP_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .withEntandoApp("some-namespace", "some-app")
                .withEntandoPlugin("antoher-namespace", "some-plugin")
                .endSpec()
                .build();
        //When
        //We are not using the mock server here because of a known bug

        getClient().customResources(EntandoAppPluginLink.class).inNamespace(MY_APP_NAMESPACE).create(entandoAppPluginLink);

        EntandoAppPluginLink actual = getClient().customResources(EntandoAppPluginLink.class).inNamespace(MY_APP_NAMESPACE)
                .withName(MY_APP_PLUGIN_LINK)
                .patch(new EntandoAppPluginLinkBuilder(
                        getClient().customResources(EntandoAppPluginLink.class).inNamespace(MY_APP_NAMESPACE).withName(MY_APP_PLUGIN_LINK)
                                .fromServer().get())
                        .editMetadata().addToLabels("my-label", "my-value")
                        .endMetadata()
                        .editSpec()
                        .withEntandoApp(MY_APP_NAMESPACE, MY_APP)
                        .withEntandoPlugin(MY_PLUGIN_NAMESPACE, MY_PLUGIN)
                        .endSpec()
                        .build());
        //Then
        assertThat(actual.getSpec().getEntandoAppName(), is(MY_APP));
        assertThat(actual.getSpec().getEntandoAppNamespace().get(), is(MY_APP_NAMESPACE));
        assertThat(actual.getSpec().getEntandoPluginName(), is(MY_PLUGIN));
        assertThat(actual.getSpec().getEntandoPluginNamespace().get(), is(MY_PLUGIN_NAMESPACE));
        assertThat(actual.getMetadata().getName(), is(MY_APP_PLUGIN_LINK));
        assertThat(actual.getStatus(), is(notNullValue()));
    }

}
