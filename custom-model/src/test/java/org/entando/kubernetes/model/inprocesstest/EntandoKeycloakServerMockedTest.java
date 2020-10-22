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

import static org.junit.Assert.assertNotEquals;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import org.entando.kubernetes.model.AbstractEntandoKeycloakServerTest;
import org.entando.kubernetes.model.keycloakserver.EntandoKeycloakServerBuilder;
import org.junit.Rule;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;

@EnableRuleMigrationSupport
@Tags({@Tag("in-process"), @Tag("pre-deployment")})
public class EntandoKeycloakServerMockedTest extends AbstractEntandoKeycloakServerTest {

    @Rule
    public KubernetesServer server = new KubernetesServer(false, true);

    @Override
    public KubernetesClient getClient() {
        return this.server.getClient();
    }

    @Test
    public void testOverriddenEqualsMethods() {
        //The ObjectMetaBuilder's equals method is broken. There is no way to fix it. 
        // These tests just verify that inequality corresponds with hashcode
        EntandoKeycloakServerBuilder builder = new EntandoKeycloakServerBuilder().editMetadata().withNamespace("ns").withName("name")
                .endMetadata();
        assertNotEquals(builder.editMetadata(), builder.editMetadata());
        assertNotEquals(builder.editMetadata().hashCode(), builder.editMetadata().hashCode());
    }
}
