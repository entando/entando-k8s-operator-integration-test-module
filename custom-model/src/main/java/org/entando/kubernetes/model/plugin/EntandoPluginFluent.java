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

package org.entando.kubernetes.model.plugin;

import io.fabric8.kubernetes.api.builder.Fluent;
import io.fabric8.kubernetes.api.builder.Nested;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import org.entando.kubernetes.model.EntandoBaseFluent;

public class EntandoPluginFluent<A extends EntandoPluginFluent<A>> extends EntandoBaseFluent<A> implements Fluent<A> {

    protected EntandoPluginSpecBuilder spec;

    protected EntandoPluginFluent() {
        this(new ObjectMetaBuilder(), new EntandoPluginSpecBuilder());
    }

    protected EntandoPluginFluent(EntandoPluginSpec spec, ObjectMeta objectMeta) {
        this(new ObjectMetaBuilder(objectMeta), new EntandoPluginSpecBuilder(spec));
    }

    private EntandoPluginFluent(ObjectMetaBuilder metadata, EntandoPluginSpecBuilder spec) {
        super(metadata);
        this.spec = spec;
    }

    @SuppressWarnings("unchecked")
    public NestedEntandoPluginSpecFluent<A> editSpec() {
        return new NestedEntandoPluginSpecFluent<>((A) this, this.spec.build());
    }

    @SuppressWarnings("unchecked")
    public NestedEntandoPluginSpecFluent<A> withNewSpec() {
        return new NestedEntandoPluginSpecFluent<>((A) this);
    }

    @SuppressWarnings("unchecked")
    public A withSpec(EntandoPluginSpec spec) {
        this.spec = new EntandoPluginSpecBuilder(spec);
        return (A) this;
    }

    public static class NestedEntandoPluginSpecFluent<N extends EntandoPluginFluent> extends
            EntandoPluginSpecFluent<NestedEntandoPluginSpecFluent<N>> implements
            Nested<N> {

        private final N parentBuilder;

        NestedEntandoPluginSpecFluent(N parentBuilder, EntandoPluginSpec item) {
            super(item);
            this.parentBuilder = parentBuilder;
        }

        public NestedEntandoPluginSpecFluent(N parentBuilder) {
            super();
            this.parentBuilder = parentBuilder;
        }

        @SuppressWarnings("unchecked")
        @Override
        public N and() {
            return (N) parentBuilder.withSpec(this.build());
        }

        public N endSpec() {
            return this.and();
        }
    }

}
