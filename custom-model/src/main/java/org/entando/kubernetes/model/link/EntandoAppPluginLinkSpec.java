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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;

@JsonSerialize
@JsonDeserialize
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
public class EntandoAppPluginLinkSpec implements Serializable {

    private String entandoAppNamespace;
    private String entandoAppName;
    private String entandoPluginNamespace;
    private String entandoPluginName;

    public EntandoAppPluginLinkSpec() {
        //Required for JSON deserialization
    }

    public EntandoAppPluginLinkSpec(String entandoAppNamespace, String entandoAppName, String entandoPluginNamespace,
            String entandoPluginName) {
        this.entandoAppNamespace = entandoAppNamespace;
        this.entandoAppName = entandoAppName;
        this.entandoPluginNamespace = entandoPluginNamespace;
        this.entandoPluginName = entandoPluginName;
    }

    public String getEntandoAppName() {
        return entandoAppName;
    }

    public String getEntandoAppNamespace() {
        return entandoAppNamespace;
    }

    public String getEntandoPluginName() {
        return entandoPluginName;
    }

    public String getEntandoPluginNamespace() {
        return entandoPluginNamespace;
    }

}