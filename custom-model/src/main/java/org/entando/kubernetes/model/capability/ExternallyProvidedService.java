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

package org.entando.kubernetes.model.capability;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Optional;

@JsonSerialize
@JsonDeserialize
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(
        fieldVisibility = Visibility.ANY,
        isGetterVisibility = Visibility.NONE,
        getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE
)
public class ExternallyProvidedService {

    private final String host;
    private final Integer port;
    private final String adminSecretName;
    private final String path;
    private final Boolean requiresDirectConnection;

    public ExternallyProvidedService(@JsonProperty("host") String host,
            @JsonProperty("port") Integer port,
            @JsonProperty("adminSecretName") String adminSecretName,
            @JsonProperty("path") String path,
            @JsonProperty("requiresDirectConnection") Boolean requiresDirectConnection) {
        this.host = host;
        this.port = port;
        this.adminSecretName = adminSecretName;
        this.path = path;
        this.requiresDirectConnection = requiresDirectConnection;
    }

    public String getHost() {
        return host;
    }

    public Optional<Integer> getPort() {
        return Optional.ofNullable(port);
    }

    public Optional<String> getPath() {
        return Optional.ofNullable(path);
    }

    public Optional<Boolean> getRequiresDirectConnection() {
        return Optional.ofNullable(requiresDirectConnection);
    }

    public String getAdminSecretName() {
        return adminSecretName;
    }
}
