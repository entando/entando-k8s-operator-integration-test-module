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

package org.entando.kubernetes.model.externaldatabase;

import io.fabric8.kubernetes.api.builder.Fluent;
import io.fabric8.kubernetes.api.builder.Nested;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.internal.KubernetesDeserializer;
import org.entando.kubernetes.model.EntandoBaseFluent;

public class EntandoExternalDBFluent<A extends EntandoExternalDBFluent<A>> extends EntandoBaseFluent<A> implements Fluent<A> {

    protected EntandoExternalDBSpecBuilder spec;

    protected EntandoExternalDBFluent() {
        this(new ObjectMetaBuilder(), new EntandoExternalDBSpecBuilder());
    }

    protected EntandoExternalDBFluent(EntandoExternalDBSpec spec, ObjectMeta objectMeta) {
        this(new ObjectMetaBuilder(objectMeta), new EntandoExternalDBSpecBuilder(spec));
    }

    private EntandoExternalDBFluent(ObjectMetaBuilder metadata, EntandoExternalDBSpecBuilder spec) {
        super(metadata);
        KubernetesDeserializer.registerCustomKind("entando.org/v1#EntandoExternalDB", EntandoExternalDB.class);
        this.spec = spec;
    }

    @SuppressWarnings("unchecked")
    public SpecNestedImpl<A> editSpec() {
        return new SpecNestedImpl<>((A) this, this.spec.build());
    }

    @SuppressWarnings("unchecked")
    public SpecNestedImpl<A> withNewSpec() {
        return new SpecNestedImpl<>((A) this);
    }

    @SuppressWarnings("unchecked")
    public A withSpec(EntandoExternalDBSpec spec) {
        this.spec = new EntandoExternalDBSpecBuilder(spec);
        return (A) this;
    }

    public static class SpecNestedImpl<N extends EntandoExternalDBFluent> extends
            EntandoExternalDBSpecFluent<SpecNestedImpl<N>> implements
            Nested<N> {

        private final N parentBuilder;

        SpecNestedImpl(N parentBuilder, EntandoExternalDBSpec item) {
            super(item);
            this.parentBuilder = parentBuilder;
        }

        public SpecNestedImpl(N parentBuilder) {
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
