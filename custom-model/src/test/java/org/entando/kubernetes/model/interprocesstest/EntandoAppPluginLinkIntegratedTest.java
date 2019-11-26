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
import org.entando.kubernetes.model.AbstractEntandoAppPluginLinkTest;
import org.entando.kubernetes.model.link.DoneableEntandoAppPluginLink;
import org.entando.kubernetes.model.link.EntandoAppPluginLink;
import org.junit.jupiter.api.Tag;

@Tag("inter-process")
public class EntandoAppPluginLinkIntegratedTest extends AbstractEntandoAppPluginLinkTest {

    private final KubernetesClient client = new AutoAdaptableKubernetesClient();

    @Override
    protected DoneableEntandoAppPluginLink editEntandoAppPluginLink(EntandoAppPluginLink entandoPlugin) throws InterruptedException {
        entandoAppPluginLinks().inNamespace(MY_APP_NAMESPACE).create(entandoPlugin);
        return entandoAppPluginLinks().inNamespace(MY_APP_NAMESPACE).withName(MY_PLUGIN).edit();
    }

    @Override
    public KubernetesClient getClient() {
        return client;
    }

}
