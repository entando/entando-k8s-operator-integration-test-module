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

package org.entando.kubernetes.model.app;

import io.fabric8.kubernetes.api.builder.Nested;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import org.entando.kubernetes.model.EntandoBaseFluent;

public class EntandoAppFluent<A extends EntandoAppFluent<A>> extends EntandoBaseFluent<A> {

    protected EntandoAppSpecBuilder spec;

    protected EntandoAppFluent() {
        this(new ObjectMetaBuilder(), new EntandoAppSpecBuilder());
    }

    protected EntandoAppFluent(EntandoAppSpec spec, ObjectMeta objectMeta) {
        this(new ObjectMetaBuilder(objectMeta), new EntandoAppSpecBuilder(spec));
    }

    private EntandoAppFluent(ObjectMetaBuilder metadata, EntandoAppSpecBuilder spec) {
        super(metadata);
        this.spec = spec;
    }

    public SpecNestedImpl<A> editSpec() {
        return new SpecNestedImpl<>((A) this, this.spec.build());
    }

    public SpecNestedImpl<A> withNewSpec() {
        return new SpecNestedImpl<>((A) this);
    }

    public A withSpec(EntandoAppSpec spec) {
        this.spec = new EntandoAppSpecBuilder(spec);
        return (A) this;
    }

    public static class SpecNestedImpl<N extends EntandoAppFluent> extends EntandoAppSpecFluent<SpecNestedImpl<N>> implements Nested<N> {

        private final N parentBuilder;

        SpecNestedImpl(N parentBuilder, EntandoAppSpec item) {
            super(item);
            this.parentBuilder = parentBuilder;
        }

        public SpecNestedImpl(N parentBuilder) {
            super();
            this.parentBuilder = parentBuilder;
        }

        @Override
        public N and() {
            return (N) parentBuilder.withSpec(this.build());
        }

        public N endSpec() {
            return this.and();
        }
    }

}
