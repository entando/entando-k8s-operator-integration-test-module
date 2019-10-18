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

package org.entando.kubernetes.model.link;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import org.entando.kubernetes.model.EntandoCustomResourceStatus;
import org.entando.kubernetes.model.app.EntandoBaseCustomResource;

@JsonSerialize
@JsonDeserialize
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
public class EntandoAppPluginLink extends EntandoBaseCustomResource {

    public static final String CRD_NAME = "entandoapppluginlinks.entando.org";
    private EntandoAppPluginLinkSpec spec;

    public EntandoAppPluginLink() {
        this(null);
    }

    public EntandoAppPluginLink(EntandoAppPluginLinkSpec spec) {
        this(new ObjectMeta(), spec);
    }

    public EntandoAppPluginLink(ObjectMeta meta, EntandoAppPluginLinkSpec spec) {
        this(meta, spec, null);
    }

    public EntandoAppPluginLink(ObjectMeta meta, EntandoAppPluginLinkSpec spec, EntandoCustomResourceStatus entandoStatus) {
        super(entandoStatus);
        super.setMetadata(meta);
        this.spec = spec;
    }

    public EntandoAppPluginLinkSpec getSpec() {
        return spec;
    }

}