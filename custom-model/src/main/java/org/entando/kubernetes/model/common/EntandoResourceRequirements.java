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

import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceRequirements;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
public class EntandoResourceRequirements extends ResourceRequirements implements Serializable {

    private String storageRequest;
    private String storageLimit;
    private String memoryRequest;
    private String memoryLimit;
    private String cpuRequest;
    private String cpuLimit;
    private String fileUploadLimit;

    public EntandoResourceRequirements() {
    }

    public EntandoResourceRequirements(@JsonProperty("storageRequest") String storageRequest,
            @JsonProperty("storageLimit") String storageLimit,
            @JsonProperty("memoryRequest") String memoryRequest,
            @JsonProperty("memoryLimit") String memoryLimit,
            @JsonProperty("cpuRequest") String cpuRequest,
            @JsonProperty("cpuLimit") String cpuLimit,
            @JsonProperty("fileUploadLimit") String fileUploadLimit,
            @JsonProperty("limits") Map<String, Quantity> limits,
            @JsonProperty("requests") Map<String, Quantity> requests) {
        super(limits, requests);
        this.storageRequest = storageRequest;
        this.storageLimit = storageLimit;
        this.memoryRequest = memoryRequest;
        this.memoryLimit = memoryLimit;
        this.cpuRequest = cpuRequest;
        this.cpuLimit = cpuLimit;
        this.fileUploadLimit = fileUploadLimit;
    }

    public Optional<String> getStorageRequest() {
        return getRequest("storage").or(() -> ofNullable(storageRequest));
    }

    public Optional<String> getStorageLimit() {
        return getLimit("storage").or(() -> ofNullable(storageLimit));
    }

    public Optional<String> getMemoryRequest() {
        return getRequest("memory").or(() -> ofNullable(memoryRequest));
    }

    public Optional<String> getMemoryLimit() {
        return getLimit("memory").or(() -> ofNullable(memoryLimit));
    }

    public Optional<String> getCpuRequest() {
        return getRequest("cpu").or(() -> ofNullable(cpuRequest));
    }

    public Optional<String> getCpuLimit() {
        return getLimit("cpu").or(() -> ofNullable(cpuLimit));
    }

    public Optional<String> getFileUploadLimit() {
        return getLimit("fileUpload").or(() -> ofNullable(fileUploadLimit));
    }

    private Optional<String> getRequest(String name) {
        return ofNullable(getRequests()).flatMap(m -> ofNullable(m.get(name))).map(Quantity::toString);
    }

    private Optional<String> getLimit(String name) {
        return ofNullable(getLimits()).flatMap(m -> ofNullable(m.get(name))).map(Quantity::toString);
    }
}
