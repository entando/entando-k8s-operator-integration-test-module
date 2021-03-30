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

package org.entando.kubernetes.controller.spi.container;

import java.util.Optional;
import org.entando.kubernetes.controller.spi.common.EntandoOperatorSpiConfig;

public interface PersistentVolumeAware extends DeployableContainer {

    String getVolumeMountPath();

    default int getStorageLimitMebibytes() {
        return 2048;
    }

    default Optional<String> getStorageClass() {
        return EntandoOperatorSpiConfig.getDefaultClusteredStorageClass();
    }

    default Optional<String> getAccessMode() {
        return EntandoOperatorSpiConfig.getPvcAccessModeOverride();
    }

}
