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

package org.entando.kubernetes.model.app;

import org.entando.kubernetes.model.EntandoDeploymentSpecBuilder;
import org.entando.kubernetes.model.KeycloakToUse;

public abstract class KeycloakAwareSpecBuilder<N extends KeycloakAwareSpecBuilder> extends EntandoDeploymentSpecBuilder<N> {

    protected KeycloakToUse keycloakToUse;

    protected KeycloakAwareSpecBuilder(KeycloakAwareSpec spec) {
        super(spec);
        this.keycloakToUse = spec.getKeycloakToUse().orElse(null);
    }

    protected KeycloakAwareSpecBuilder() {
    }
    public N withKeycloakToUse(String namespace, String name) {
        return withKeycloakToUse(namespace, name, null);
    }
    public N withKeycloakToUse(String namespace, String name, String realm) {
        return withKeycloakToUse(namespace, name, realm, null);
    }

    public N withKeycloakToUse(String namespace, String name, String realm, String publicClientId) {
        this.keycloakToUse = new KeycloakToUse(namespace, name, realm, publicClientId);
        return thisAsN();
    }
}
