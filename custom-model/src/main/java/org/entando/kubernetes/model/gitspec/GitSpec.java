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

package org.entando.kubernetes.model.gitspec;

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
@JsonDeserialize()
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)

public class GitSpec implements Serializable {

    private String repository;
    private String secretName;
    private String targetRef;
    private GitResponsibility responsibility;

    public GitSpec() {
        super();
    }

    /**
     * Only for use from the builder.
     */
    @JsonCreator
    public GitSpec(@JsonProperty("repository") String repository, @JsonProperty("secretName") String secretName,
            @JsonProperty("targetRef") String targetRef, @JsonProperty("backupResponsibility") GitResponsibility responsibility) {
        this.repository = repository;
        this.secretName = secretName;
        this.targetRef = targetRef;
        this.responsibility = responsibility;
    }

    public String getRepository() {
        return repository;
    }

    public Optional<String> getSecretName() {
        return Optional.ofNullable(secretName);
    }

    public Optional<String> getTargetRef() {
        return Optional.ofNullable(targetRef);
    }

    public GitResponsibility getResponsibility() {
        return responsibility;
    }
}
