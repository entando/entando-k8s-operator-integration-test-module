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

package org.entando.kubernetes.model.externaldatabase;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.CustomResource;
import org.entando.kubernetes.model.EntandoCustomResource;
import org.entando.kubernetes.model.EntandoCustomResourceStatus;

@JsonSerialize
@JsonDeserialize
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
public class ExternalDatabase extends CustomResource implements EntandoCustomResource {

    public static final String CRD_NAME = "externaldatabases.entando.org";

    private ExternalDatabaseSpec spec;
    private EntandoCustomResourceStatus entandoStatus;

    public ExternalDatabase() {
        super();
        setApiVersion("entando.org/v1alpha1");
    }

    public ExternalDatabase(ExternalDatabaseSpec spec) {
        this();
        this.spec = spec;
    }

    public ExternalDatabase(ObjectMeta metadata, ExternalDatabaseSpec spec) {
        this(spec);
        super.setMetadata(metadata);
    }

    public ExternalDatabase(ObjectMeta metadata, ExternalDatabaseSpec spec, EntandoCustomResourceStatus status) {
        this(metadata, spec);
        entandoStatus = status;
    }

    public ExternalDatabaseSpec getSpec() {
        return spec;
    }

    public void setSpec(ExternalDatabaseSpec spec) {
        this.spec = spec;
    }

    @Override
    public EntandoCustomResourceStatus getStatus() {
        return this.entandoStatus == null ? this.entandoStatus = new EntandoCustomResourceStatus() : this.entandoStatus;
    }

    @Override
    public void setStatus(EntandoCustomResourceStatus status) {
        this.entandoStatus = status;
    }

}
