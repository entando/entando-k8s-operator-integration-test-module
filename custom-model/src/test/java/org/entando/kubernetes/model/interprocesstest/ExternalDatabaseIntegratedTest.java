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

package org.entando.kubernetes.model.interprocesstest;

import io.fabric8.kubernetes.client.AutoAdaptableKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.entando.kubernetes.model.AbstractExternalDatabaseTest;
import org.entando.kubernetes.model.externaldatabase.DoneableExternalDatabase;
import org.entando.kubernetes.model.externaldatabase.ExternalDatabase;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;

@EnableRuleMigrationSupport
@Tag("inter-process")
@SuppressWarnings("PMD.TestClassWithoutTestCases")
//Because PMD doesn't know they are inherited
public class ExternalDatabaseIntegratedTest extends AbstractExternalDatabaseTest {

    private final KubernetesClient client = new AutoAdaptableKubernetesClient();

    @Override
    public KubernetesClient getClient() {
        return client;
    }

    @Override
    protected DoneableExternalDatabase editExternalDatabase(ExternalDatabase externalDatabase) throws InterruptedException {
        externalDatabases().inNamespace(MY_NAMESPACE).create(externalDatabase);
        return externalDatabases().inNamespace(MY_NAMESPACE).withName(MY_EXTERNAL_DATABASE).edit();
    }

}
