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

package org.entando.kubernetes.model.inprocesstest;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import org.entando.kubernetes.model.AbstractEntandoAppPluginLinkTest;
import org.entando.kubernetes.model.link.DoneableEntandoAppPluginLink;
import org.entando.kubernetes.model.link.EntandoAppPluginLink;
import org.entando.kubernetes.model.link.EntandoAppPluginLinkBuilder;
import org.junit.Rule;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;

@EnableRuleMigrationSupport
@Tag("in-process")
@SuppressWarnings("PMD.TestClassWithoutTestCases")
//Because PMD doesn't know they are inherited
public class EntandoAppPluginLinkMockedTest extends AbstractEntandoAppPluginLinkTest {

    @Rule
    public KubernetesServer server = new KubernetesServer(false, true);

    @Override
    protected DoneableEntandoAppPluginLink editEntandoAppPluginLink(EntandoAppPluginLink entandoPlugin) {
        return new DoneableEntandoAppPluginLink(entandoPlugin, builtEntandoAppPluginLink -> builtEntandoAppPluginLink);
    }

    @Override
    public KubernetesClient getClient() {
        return server.getClient();
    }

    @Test
    public void testOverriddenEqualsMethods() {
        //The ObjectMetaBuilder's equals method is broken. There is no way to fix it. 
        // These tests just verify that inequality corresponds with hashcode
        EntandoAppPluginLinkBuilder builder = new EntandoAppPluginLinkBuilder().editMetadata().withNamespace("ns").withName("name")
                .endMetadata();
        assertNotEquals(builder.editMetadata(), builder.editMetadata());
        assertNotEquals(builder.editMetadata().hashCode(), builder.editMetadata().hashCode());
    }

}
