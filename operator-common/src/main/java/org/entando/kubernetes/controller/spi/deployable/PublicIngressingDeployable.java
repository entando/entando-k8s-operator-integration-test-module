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

package org.entando.kubernetes.controller.spi.deployable;

import java.util.Optional;
import org.entando.kubernetes.controller.spi.common.KeycloakPreference;
import org.entando.kubernetes.controller.spi.container.KeycloakConnectionConfig;
import org.entando.kubernetes.controller.spi.container.KeycloakName;
import org.entando.kubernetes.controller.spi.result.ExposedDeploymentResult;
import org.entando.kubernetes.model.KeycloakToUse;

public interface PublicIngressingDeployable<T extends ExposedDeploymentResult<T>> extends IngressingDeployable<T>, KeycloakPreference {

    KeycloakConnectionConfig getKeycloakConnectionConfig();

    Optional<KeycloakToUse> getPreferredKeycloakToUse();

    default String getKeycloakRealmToUse() {
        return KeycloakName.ofTheRealm(this);

    }

    default String getPublicClientIdToUse() {
        return KeycloakName.ofThePublicClient(this);

    }
}
