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

public class EntandoAppPluginLinkSpecFluent<N extends EntandoAppPluginLinkSpecFluent> {

    private String entandoAppName;
    private String entandoAppNamespace;
    private String entandoPluginName;
    private String entandoPluginNamespace;

    public EntandoAppPluginLinkSpecFluent(EntandoAppPluginLinkSpec spec) {
        this.entandoAppNamespace = spec.getEntandoAppNamespace().orElse(null);
        this.entandoAppName = spec.getEntandoAppName();
        this.entandoPluginNamespace = spec.getEntandoPluginNamespace().orElse(null);
        this.entandoPluginName = spec.getEntandoPluginName();

    }

    public EntandoAppPluginLinkSpecFluent() {

    }

    @SuppressWarnings("unchecked")
    public N withEntandoApp(String entandoAppNamespace, String entandoAppName) {
        this.entandoAppNamespace = entandoAppNamespace;
        this.entandoAppName = entandoAppName;
        return (N) this;
    }

    @SuppressWarnings("unchecked")
    public N withEntandoPlugin(String entandoPluginNamespace, String entandoPluginName) {
        this.entandoPluginNamespace = entandoPluginNamespace;
        this.entandoPluginName = entandoPluginName;
        return (N) this;
    }

    public EntandoAppPluginLinkSpec build() {
        return new EntandoAppPluginLinkSpec(entandoAppNamespace, entandoAppName, entandoPluginNamespace, entandoPluginName);
    }
}
