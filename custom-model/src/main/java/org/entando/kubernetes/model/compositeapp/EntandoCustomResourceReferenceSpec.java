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

package org.entando.kubernetes.model.compositeapp;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;
import java.util.Optional;

@JsonSerialize
@JsonDeserialize
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
public class EntandoCustomResourceReferenceSpec implements Serializable {

    private String targetKind;
    private String targetNamespace;
    private String targetName;

    public EntandoCustomResourceReferenceSpec() {
        //for serialization
    }

    @JsonCreator
    public EntandoCustomResourceReferenceSpec(@JsonProperty("targetKind") String targetKind,
            @JsonProperty("targetNamespace") String targetNamespace,
            @JsonProperty("targetName") String targetName) {
        this.targetKind = targetKind;
        this.targetNamespace = targetNamespace;
        this.targetName = targetName;
    }

    public String getTargetKind() {
        return targetKind;
    }

    public String getTargetName() {
        return targetName;
    }

    public Optional<String> getTargetNamespace() {
        return Optional.ofNullable(targetNamespace);
    }
}
