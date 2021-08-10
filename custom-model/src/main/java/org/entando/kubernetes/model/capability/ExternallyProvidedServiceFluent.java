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

public class ExternallyProvidedServiceFluent<N extends ExternallyProvidedServiceFluent<N>> {

    private String host;
    private Integer port;
    private String adminSecretName;
    private Boolean requiresDirectConnection;
    private String path;

    public ExternallyProvidedServiceFluent() {

    }

    public ExternallyProvidedServiceFluent(ExternallyProvidedService service) {
        this.host = service.getHost();
        this.port = service.getPort().orElse(null);
        this.adminSecretName = service.getAdminSecretName();
        this.path = service.getPath().orElse(null);
        this.requiresDirectConnection = service.getRequiresDirectConnection().orElse(null);
    }

    public N withPath(String path) {
        this.path = path;
        return thisAsN();
    }

    public N withHost(String host) {
        this.host = host;
        return thisAsN();
    }

    public N withPort(Integer port) {
        this.port = port;
        return thisAsN();
    }

    public N withAdminSecretName(String adminSecretName) {
        this.adminSecretName = adminSecretName;
        return thisAsN();
    }

    public N withRequiresDirectConnection(Boolean requiresDirectConnection) {
        this.requiresDirectConnection = requiresDirectConnection;
        return thisAsN();
    }

    @SuppressWarnings("unchecked")
    protected N thisAsN() {
        return (N) this;
    }

    public ExternallyProvidedService build() {
        return new ExternallyProvidedService(host, port, adminSecretName, path, requiresDirectConnection);
    }

}
