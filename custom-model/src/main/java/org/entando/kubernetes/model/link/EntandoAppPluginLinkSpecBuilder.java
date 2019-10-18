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

public class EntandoAppPluginLinkSpecBuilder<N extends EntandoAppPluginLinkSpecBuilder> {

    private String entandoAppName;
    private String entandoAppNamespace;
    private String entandoPluginName;
    private String entandoPluginNamespace;

    public EntandoAppPluginLinkSpecBuilder() {
        //Useful
    }

    public EntandoAppPluginLinkSpecBuilder(EntandoAppPluginLinkSpec spec) {
        this.entandoAppNamespace = spec.getEntandoAppNamespace();
        this.entandoAppName = spec.getEntandoAppName();
        this.entandoPluginNamespace = spec.getEntandoPluginNamespace();
        this.entandoPluginName = spec.getEntandoPluginName();
    }

    public N withEntandoApp(String entandoAppNamespace, String entandoAppName) {
        this.entandoAppNamespace = entandoAppNamespace;
        this.entandoAppName = entandoAppName;
        return (N) this;
    }

    public N withEntandoPlugin(String entandoPluginNamespace, String entandoPluginName) {
        this.entandoPluginNamespace = entandoPluginNamespace;
        this.entandoPluginName = entandoPluginName;
        return (N) this;
    }

    public EntandoAppPluginLinkSpec build() {
        return new EntandoAppPluginLinkSpec(entandoAppNamespace, entandoAppName, entandoPluginNamespace, entandoPluginName);
    }
}
