/*
 *
 *  * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
 *  *
 *  * This library is free software; you can redistribute it and/or modify it under
 *  * the terms of the GNU Lesser General Public License as published by the Free
 *  * Software Foundation; either version 2.1 of the License, or (at your option)
 *  * any later version.
 *  *
 *  * This library is distributed in the hope that it will be useful, but WITHOUT
 *  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 *  * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 *  * details.
 *
 */

package org.entando.kubernetes.model.inprocesstest;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import org.entando.kubernetes.model.AbstractKeycloakServerTest;
import org.entando.kubernetes.model.keycloakserver.DoneableKeycloakServer;
import org.entando.kubernetes.model.keycloakserver.KeycloakServer;
import org.junit.Rule;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;

@EnableRuleMigrationSupport
@Tag("in-process")
@SuppressWarnings("PMD.TestClassWithoutTestCases")
//Because PMD doesn't know they are inherited
public class KeycloakServerMockedTest extends AbstractKeycloakServerTest {

    @Rule
    public KubernetesServer server = new KubernetesServer(false, true);

    @Override
    public KubernetesClient getClient() {
        return this.server.getClient();
    }

    @Override
    protected DoneableKeycloakServer editKeycloakServer(KeycloakServer keycloakServer) {
        return new DoneableKeycloakServer(keycloakServer, builtKeycloakServer -> builtKeycloakServer);
    }

}
