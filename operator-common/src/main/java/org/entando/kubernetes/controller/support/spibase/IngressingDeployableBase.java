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

package org.entando.kubernetes.controller.support.spibase;

import java.util.Optional;
import org.entando.kubernetes.controller.spi.deployable.IngressingDeployable;
import org.entando.kubernetes.controller.spi.result.ExposedDeploymentResult;
import org.entando.kubernetes.model.EntandoBaseCustomResource;
import org.entando.kubernetes.model.EntandoIngressingDeploymentSpec;
import org.entando.kubernetes.model.EntandoResourceRequirements;

public interface IngressingDeployableBase<T extends ExposedDeploymentResult<T>> extends IngressingDeployable<T> {

    @Override
    @SuppressWarnings("unchecked")
    default Optional<String> getFileUploadLimit() {
        EntandoBaseCustomResource<? extends EntandoIngressingDeploymentSpec> r =
                (EntandoBaseCustomResource<? extends EntandoIngressingDeploymentSpec>) getCustomResource();
        return r.getSpec().getResourceRequirements().flatMap(EntandoResourceRequirements::getFileUploadLimit);
    }

    @Override
    @SuppressWarnings("unchecked")
    default Optional<String> getTlsSecretName() {
        EntandoBaseCustomResource<? extends EntandoIngressingDeploymentSpec> r =
                (EntandoBaseCustomResource<? extends EntandoIngressingDeploymentSpec>) getCustomResource();
        return r.getSpec().getTlsSecretName();
    }

    @Override
    @SuppressWarnings("unchecked")
    default Optional<String> getIngressHostName() {
        EntandoBaseCustomResource<? extends EntandoIngressingDeploymentSpec> r =
                (EntandoBaseCustomResource<? extends EntandoIngressingDeploymentSpec>) getCustomResource();
        return r.getSpec().getIngressHostName();
    }

}
