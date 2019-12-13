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

public class EntandoExternalDatabaseFluent<A extends EntandoExternalDatabaseFluent<A>> extends EntandoBaseFluent<A> implements Fluent<A> {

    protected EntandoExternalDatabaseSpecBuilder spec;

    protected EntandoExternalDatabaseFluent() {
        this(new ObjectMetaBuilder(), new EntandoExternalDatabaseSpecBuilder());
    }

    protected EntandoExternalDatabaseFluent(EntandoExternalDatabaseSpec spec, ObjectMeta objectMeta) {
        this(new ObjectMetaBuilder(objectMeta), new EntandoExternalDatabaseSpecBuilder(spec));
    }

    private EntandoExternalDatabaseFluent(ObjectMetaBuilder metadata, EntandoExternalDatabaseSpecBuilder spec) {
        super(metadata);
        KubernetesDeserializer.registerCustomKind("entando.org/v1alpha1#EntandoExternalDB", EntandoExternalDatabase.class);
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
    public A withSpec(EntandoExternalDatabaseSpec spec) {
        this.spec = new EntandoExternalDatabaseSpecBuilder(spec);
        return (A) this;
    }

    public static class SpecNestedImpl<N extends EntandoExternalDatabaseFluent> extends EntandoExternalDatabaseSpecFluent<SpecNestedImpl<N>> implements
            Nested<N> {

        private final N parentBuilder;

        SpecNestedImpl(N parentBuilder, EntandoExternalDatabaseSpec item) {
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
