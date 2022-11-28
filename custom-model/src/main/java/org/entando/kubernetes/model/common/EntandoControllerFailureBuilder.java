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

package org.entando.kubernetes.model.common;

import io.fabric8.kubernetes.api.model.HasMetadata;

public class EntandoControllerFailureBuilder {

    private String failedObjectApiVersion;
    private String failedObjectKind;
    private String failedObjectNamespace;
    private String failedObjectName;
    private String message;
    private String detailMessage;

    public EntandoControllerFailure build() {
        return new EntandoControllerFailure(failedObjectApiVersion, failedObjectKind, failedObjectNamespace, failedObjectName, message,
                detailMessage);
    }

    public EntandoControllerFailureBuilder withFailedObjectKind(String failedObjectKind) {
        this.failedObjectKind = failedObjectKind;
        return this;
    }

    public EntandoControllerFailureBuilder withFailedObjectName(String failedObjectName) {
        this.failedObjectName = failedObjectName;
        return this;
    }

    public EntandoControllerFailureBuilder withFailedObjectNamespace(String failedObjectNamespace) {
        this.failedObjectNamespace = failedObjectNamespace;
        return this;
    }

    public EntandoControllerFailureBuilder withMessage(String message) {
        this.message = message;
        return this;
    }

    public EntandoControllerFailureBuilder withDetailMessage(String detailMessage) {
        this.detailMessage = detailMessage;
        return this;
    }

    public EntandoControllerFailureBuilder withFailedObjectApiVersion(String failedObjectApiVersion) {
        this.failedObjectApiVersion = failedObjectApiVersion;
        return this;
    }

    public EntandoControllerFailureBuilder withFailedObject(HasMetadata failedObject) {
        return withFailedObjectApiVersion(failedObject.getApiVersion())
                .withFailedObjectKind(failedObject.getKind())
                .withFailedObjectNamespace(failedObject.getMetadata().getNamespace())
                .withFailedObjectName(failedObject.getMetadata().getName());
    }
}
