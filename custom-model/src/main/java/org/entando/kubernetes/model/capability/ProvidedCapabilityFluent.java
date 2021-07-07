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

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import org.entando.kubernetes.model.common.EntandoFluent;

public class ProvidedCapabilityFluent<F extends ProvidedCapabilityFluent<F>> extends EntandoFluent<F> {

    protected CapabilityRequirementBuilder spec;

    protected ProvidedCapabilityFluent() {
        this(new ObjectMetaBuilder(), new CapabilityRequirementBuilder());
    }

    protected ProvidedCapabilityFluent(CapabilityRequirement spec, ObjectMeta objectMeta) {
        this(new ObjectMetaBuilder(objectMeta), new CapabilityRequirementBuilder(spec));
    }

    private ProvidedCapabilityFluent(ObjectMetaBuilder metadata, CapabilityRequirementBuilder spec) {
        super(metadata);
        this.spec = spec;
    }

    public NestedCapabilityRequirementFluent<F> editSpec() {
        return new NestedCapabilityRequirementFluent<>(thisAsF(), this.spec.build());
    }

    public NestedCapabilityRequirementFluent<F> withNewSpec() {
        return new NestedCapabilityRequirementFluent<>(thisAsF());
    }

    public F withSpec(CapabilityRequirement spec) {
        this.spec = new CapabilityRequirementBuilder(spec);
        return thisAsF();
    }

}
