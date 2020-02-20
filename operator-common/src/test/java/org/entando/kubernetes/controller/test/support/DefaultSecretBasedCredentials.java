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

package org.entando.kubernetes.controller.test.support;

import io.fabric8.kubernetes.api.model.Secret;
import org.entando.kubernetes.controller.SecretBasedCredentials;

public class DefaultSecretBasedCredentials implements SecretBasedCredentials {

    private final Secret secret;

    public DefaultSecretBasedCredentials(Secret secret) {
        this.secret = secret;
    }

    @Override
    public Secret getSecret() {
        return secret;
    }
}
