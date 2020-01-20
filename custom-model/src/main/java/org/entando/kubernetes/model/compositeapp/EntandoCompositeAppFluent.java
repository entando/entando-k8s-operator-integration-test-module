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

import io.fabric8.kubernetes.api.builder.Nested;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import org.entando.kubernetes.model.EntandoBaseFluent;

public class EntandoCompositeAppFluent<A extends EntandoCompositeAppFluent<A>> extends EntandoBaseFluent<A> {

    protected EntandoCompositeAppSpecBuilder spec;

    protected EntandoCompositeAppFluent() {
        this(new ObjectMetaBuilder(), new EntandoCompositeAppSpecBuilder());
    }

    protected EntandoCompositeAppFluent(EntandoCompositeAppSpec spec, ObjectMeta objectMeta) {
        this(new ObjectMetaBuilder(objectMeta), new EntandoCompositeAppSpecBuilder(spec));
    }

    private EntandoCompositeAppFluent(ObjectMetaBuilder metadata, EntandoCompositeAppSpecBuilder spec) {
        super(metadata);
        this.spec = spec;
    }

    public NestedEntandoCompositeAppFluent<A> editSpec() {
        return new NestedEntandoCompositeAppFluent<>(thisAsA(), this.spec.build());
    }

    public NestedEntandoCompositeAppFluent<A> withNewSpec() {
        return new NestedEntandoCompositeAppFluent<>(thisAsA());
    }

    public A withSpec(EntandoCompositeAppSpec spec) {
        this.spec = new EntandoCompositeAppSpecBuilder(spec);
        return thisAsA();
    }

    @SuppressWarnings("unchecked")
    protected A thisAsA() {
        return (A) this;
    }

    public static class NestedEntandoCompositeAppFluent<N extends EntandoCompositeAppFluent> extends
            EntandoCompositeAppSpecFluent<NestedEntandoCompositeAppFluent<N>> implements
            Nested<N> {

        private final N parentBuilder;

        NestedEntandoCompositeAppFluent(N parentBuilder, EntandoCompositeAppSpec spec) {
            super(spec);
            this.parentBuilder = parentBuilder;
        }

        public NestedEntandoCompositeAppFluent(N parentBuilder) {
            super();
            this.parentBuilder = parentBuilder;
        }

        @Override
        @SuppressWarnings("unchecked")
        public N and() {
            return (N) parentBuilder.withSpec(this.build());
        }

        public N endSpec() {
            return this.and();
        }
    }

}
