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
import java.util.Arrays;
import java.util.Collections;
import org.entando.kubernetes.model.debundle.DoneableEntandoDeBundle;
import org.entando.kubernetes.model.debundle.EntandoDeBundle;
import org.entando.kubernetes.model.debundle.EntandoDeBundleBuilder;
import org.entando.kubernetes.model.debundle.EntandoDeBundleTagBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class AbstractEntandoDeBundleTest implements CustomResourceTestUtil {

    public static final String MY_BUNDLE = "my-bundle";
    public static final String MY_DESCRIPTION = "my-description";
    public static final String SOME_TAG = "someTag";
    public static final String SOME_VALUE = "someValue";
    public static final String MY_KEYWORD = "my-keyword";
    public static final String MY_VERSION = "0.0.1";
    public static final String MY_INTEGRITY = "asdfasdf";
    public static final String MY_SHASUM = "AFGAGARG";
    public static final String MY_TARBALL = "http://npm.com/mytarball.tgz";
    protected static final String MY_NAMESPACE = TestConfig.calculateNameSpace("my-namespace");
    private EntandoResourceOperationsRegistry registry;

    @BeforeEach
    public void deleteEntandoDeBundles() {
        this.registry = new EntandoResourceOperationsRegistry(getClient());
        prepareNamespace(entandoDeBundles(), MY_NAMESPACE);
    }

    @Test
    public void testCreateEntandoDeBundle() {
        //Given
        EntandoDeBundle entandoDeBundle = new EntandoDeBundleBuilder()
                .withNewMetadata().withName(MY_BUNDLE)
                .withNamespace(MY_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .withNewDetails()
                .withDescription(MY_DESCRIPTION)
                .withName(MY_BUNDLE)
                .addNewKeyword(MY_KEYWORD)
                .addNewVersion(MY_VERSION)
                .addNewDistTag(SOME_TAG, SOME_VALUE)
                .endDetails()
                .addNewTag()
                .withIntegrity(MY_INTEGRITY)
                .withShasum(MY_SHASUM)
                .withTarball(MY_TARBALL)
                .withVersion(MY_VERSION)
                .endTag()
                .endSpec()
                .build();
        entandoDeBundles().inNamespace(MY_NAMESPACE).createNew().withMetadata(entandoDeBundle.getMetadata())
                .withSpec(entandoDeBundle.getSpec()).done();
        //When
        EntandoDeBundle actual = entandoDeBundles().inNamespace(MY_NAMESPACE).withName(MY_BUNDLE).get();

        //Then
        assertThat(actual.getSpec().getDetails().getName(), is(MY_BUNDLE));
        assertThat(actual.getSpec().getDetails().getDescription(), is(MY_DESCRIPTION));
        assertThat(actual.getSpec().getDetails().getDistTags(), is(Collections.singletonMap(SOME_TAG, SOME_VALUE)));
        assertThat(actual.getSpec().getDetails().getKeywords(), is(Collections.singletonList(MY_KEYWORD)));
        assertThat(actual.getSpec().getDetails().getVersions(), is(Collections.singletonList(MY_VERSION)));
        assertThat(actual.getSpec().getTags().get(0).getIntegrity(), is(MY_INTEGRITY));
        assertThat(actual.getSpec().getTags().get(0).getShasum(), is(MY_SHASUM));
        assertThat(actual.getSpec().getTags().get(0).getTarball(), is(MY_TARBALL));
        assertThat(actual.getSpec().getTags().get(0).getVersion(), is(MY_VERSION));
        assertThat(actual.getMetadata().getName(), is(MY_BUNDLE));
    }

    @Test
    public void testEditEntandoDeBundle() {
        //Given
        EntandoDeBundle entandoApp = new EntandoDeBundleBuilder()
                .withNewMetadata()
                .withName(MY_BUNDLE)
                .withNamespace(MY_NAMESPACE)
                .endMetadata()
                .withNewSpec()
                .withNewDetails()
                .withDescription(MY_DESCRIPTION)
                .withName(MY_BUNDLE)
                .addNewKeyword("another-keyword")
                .addNewVersion("0.0.2")
                .addNewDistTag("anotherTag", "anotherValue")
                .endDetails()
                .addNewTag()
                .withIntegrity("asdfasdfasdfasdsafsdfs")
                .withShasum("1234123412341234")
                .withTarball("sdfasdfasdfasdfas")
                .withVersion("0.0.2")
                .endTag()
                .endSpec()
                .build();
        //When
        //We are not using the mock server here because of a known bug
        EntandoDeBundle actual = editEntandoDeBundle(entandoApp)
                .editMetadata()
                .endMetadata()
                .editSpec()
                .editDetails()
                .withDescription(MY_DESCRIPTION)
                .withName(MY_BUNDLE)
                .withKeywords(Arrays.asList(MY_KEYWORD))
                .withVersions(Arrays.asList(MY_VERSION))
                .withDistTags(Collections.singletonMap(SOME_TAG, SOME_VALUE))
                .endDetails()
                .withTags(Arrays.asList(new EntandoDeBundleTagBuilder()
                        .withIntegrity(MY_INTEGRITY)
                        .withShasum(MY_SHASUM)
                        .withTarball(MY_TARBALL)
                        .withVersion(MY_VERSION)
                        .build()))
                .endSpec()
                .withPhase(EntandoDeploymentPhase.STARTED)
                .done();
        //Then
        assertThat(actual.getSpec().getDetails().getName(), is(MY_BUNDLE));
        assertThat(actual.getSpec().getDetails().getDescription(), is(MY_DESCRIPTION));
        assertThat(actual.getSpec().getDetails().getDistTags(), is(Collections.singletonMap(SOME_TAG, SOME_VALUE)));
        assertThat(actual.getSpec().getDetails().getKeywords(), is(Collections.singletonList(MY_KEYWORD)));
        assertThat(actual.getSpec().getDetails().getVersions(), is(Collections.singletonList(MY_VERSION)));
        assertThat(actual.getSpec().getTags().get(0).getIntegrity(), is(MY_INTEGRITY));
        assertThat(actual.getSpec().getTags().get(0).getShasum(), is(MY_SHASUM));
        assertThat(actual.getSpec().getTags().get(0).getTarball(), is(MY_TARBALL));
        assertThat(actual.getSpec().getTags().get(0).getVersion(), is(MY_VERSION));
        assertThat(actual.getMetadata().getName(), is(MY_BUNDLE));
    }

    protected DoneableEntandoDeBundle editEntandoDeBundle(EntandoDeBundle entandoApp) {
        entandoDeBundles().inNamespace(MY_NAMESPACE).create(entandoApp);
        return entandoDeBundles().inNamespace(MY_NAMESPACE).withName(MY_BUNDLE).edit();
    }

    protected CustomResourceOperationsImpl<EntandoDeBundle, CustomResourceList<EntandoDeBundle>,
            DoneableEntandoDeBundle> entandoDeBundles() {
        return registry.getOperations(EntandoDeBundle.class);
    }

}
