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

import io.fabric8.kubernetes.client.NamespacedKubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import org.entando.kubernetes.model.AbstractExternalDatabaseTest;
import org.entando.kubernetes.model.externaldatabase.DoneableExternalDatabase;
import org.entando.kubernetes.model.externaldatabase.ExternalDatabase;
import org.entando.kubernetes.model.externaldatabase.ExternalDatabaseBuilder;
import org.junit.Rule;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;

@EnableRuleMigrationSupport
@Tag("in-process")
@SuppressWarnings("PMD.TestClassWithoutTestCases")
//Because PMD doesn't know they are inherited
public class ExternalDatabaseMockedTest extends AbstractExternalDatabaseTest {

    @Rule
    public KubernetesServer server = new KubernetesServer(false, true);

    @Override
    public NamespacedKubernetesClient getClient() {
        return this.server.getClient();
    }

    @Override
    protected DoneableExternalDatabase editExternalDatabase(ExternalDatabase externalDatabase) {
        return new DoneableExternalDatabase(externalDatabase,
                builtExternalDatabase -> builtExternalDatabase);
    }

    @Test
    public void testOverriddenEqualsMethods() {
        //The ObjectMetaBuilder's equals method is broken. There is no way to fix it. 
        // These tests just verify that inequality corresponds with hashcode
        ExternalDatabaseBuilder builder = new ExternalDatabaseBuilder().editMetadata().withNamespace("ns").withName("name").endMetadata();
        assertNotEquals(builder.editMetadata(), builder.editMetadata());
        assertNotEquals(builder.editMetadata().hashCode(), builder.editMetadata().hashCode());
    }
}
