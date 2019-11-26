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
import org.entando.kubernetes.model.AbstractEntandoAppTest;
import org.entando.kubernetes.model.app.DoneableEntandoApp;
import org.entando.kubernetes.model.app.EntandoApp;
import org.entando.kubernetes.model.app.EntandoAppBuilder;
import org.junit.Rule;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;

@Tag("in-process")
@EnableRuleMigrationSupport
public class EntandoAppMockedTest extends AbstractEntandoAppTest {

    @Rule
    public KubernetesServer server = new KubernetesServer(false, true);

    @Override
    protected DoneableEntandoApp editEntandoApp(EntandoApp entandoApp) {
        return new DoneableEntandoApp(entandoApp, builtEntandoApp -> builtEntandoApp);
    }

    @Override
    public KubernetesClient getClient() {
        return server.getClient();
    }

    @Test
    public void testOverriddenEqualsMethods() {
        //The ObjectMetaBuilder's equals method is broken. There is no way to fix it.
        // These tests just verify that inequality corresponds with hashcode
        EntandoAppBuilder builder = new EntandoAppBuilder().editMetadata().withNamespace("ns").withName("name").endMetadata();
        assertNotEquals(builder.editMetadata(), builder.editMetadata());
        assertNotEquals(builder.editMetadata().hashCode(), builder.editMetadata().hashCode());
    }

}
