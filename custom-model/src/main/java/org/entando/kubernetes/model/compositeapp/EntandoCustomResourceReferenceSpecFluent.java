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

public abstract class EntandoCustomResourceReferenceSpecFluent<N extends EntandoCustomResourceReferenceSpecFluent> {

    protected String targetKind;
    protected String targetNamespace;
    protected String targetName;

    protected EntandoCustomResourceReferenceSpecFluent(EntandoCustomResourceReferenceSpec spec) {
        this.targetKind = spec.getTargetKind();
        this.targetName = spec.getTargetName();
        this.targetNamespace = spec.getTargetNamespace().orElse(null);
    }

    protected EntandoCustomResourceReferenceSpecFluent() {

    }

    public N withTargetKind(String targetKind) {
        this.targetKind = targetKind;
        return thisAsN();
    }

    public N withTargetName(String targetName) {
        this.targetName = targetName;
        return thisAsN();
    }

    public N withTargetNamespace(String targetNamespace) {
        this.targetNamespace = targetNamespace;
        return thisAsN();
    }

    public EntandoCustomResourceReferenceSpec build() {
        return new EntandoCustomResourceReferenceSpec(targetKind, targetNamespace, targetName);
    }

    @SuppressWarnings("unchecked")
    private N thisAsN() {
        return (N) this;
    }

}
