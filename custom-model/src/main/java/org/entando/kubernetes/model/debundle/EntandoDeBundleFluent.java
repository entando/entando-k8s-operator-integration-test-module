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

package org.entando.kubernetes.model.debundle;

import io.fabric8.kubernetes.api.builder.Nested;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import org.entando.kubernetes.model.EntandoBaseFluentImpl;

public class EntandoDeBundleFluent<A extends EntandoDeBundleFluent<A>> extends EntandoBaseFluentImpl<A> {

    protected EntandoDeBundleSpecBuilder spec;

    protected EntandoDeBundleFluent() {
        this(new ObjectMetaBuilder(), new EntandoDeBundleSpecBuilder());
    }

    protected EntandoDeBundleFluent(EntandoDeBundleSpec spec, ObjectMeta objectMeta) {
        this(new ObjectMetaBuilder(objectMeta), new EntandoDeBundleSpecBuilder(spec));
    }

    private EntandoDeBundleFluent(ObjectMetaBuilder metadata, EntandoDeBundleSpecBuilder spec) {
        super(metadata);
        this.spec = spec;
    }

    public SpecNestedImpl<A> editSpec() {
        return new SpecNestedImpl<>(thisAsA(), this.spec.build());
    }

    public SpecNestedImpl<A> withNewSpec() {
        return new SpecNestedImpl<>(thisAsA());
    }

    public A withSpec(EntandoDeBundleSpec spec) {
        this.spec = new EntandoDeBundleSpecBuilder(spec);
        return thisAsA();
    }

    @SuppressWarnings("unchecked")
    protected A thisAsA() {
        return (A) this;
    }

    public static class SpecNestedImpl<N extends EntandoDeBundleFluent> extends EntandoDeBundleSpecFluent<SpecNestedImpl<N>> implements
            Nested<N> {

        private final N parentBuilder;

        SpecNestedImpl(N parentBuilder, EntandoDeBundleSpec spec) {
            super(spec);
            this.parentBuilder = parentBuilder;
        }

        public SpecNestedImpl(N parentBuilder) {
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
