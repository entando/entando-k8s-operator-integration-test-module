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

@Deprecated
public class ExternalDatabaseFluent<A extends ExternalDatabaseFluent<A>> extends EntandoBaseFluent<A> implements Fluent<A> {

    protected ExternalDatabaseSpecBuilder spec;

    protected ExternalDatabaseFluent() {
        this(new ObjectMetaBuilder(), new ExternalDatabaseSpecBuilder());
    }

    protected ExternalDatabaseFluent(ExternalDatabaseSpec spec, ObjectMeta objectMeta) {
        this(new ObjectMetaBuilder(objectMeta), new ExternalDatabaseSpecBuilder(spec));
    }

    private ExternalDatabaseFluent(ObjectMetaBuilder metadata, ExternalDatabaseSpecBuilder spec) {
        super(metadata);
        KubernetesDeserializer.registerCustomKind("entando.org/v1#EntandoDatabaseService", ExternalDatabase.class);
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
    public A withSpec(ExternalDatabaseSpec spec) {
        this.spec = new ExternalDatabaseSpecBuilder(spec);
        return (A) this;
    }

    public static class SpecNestedImpl<N extends ExternalDatabaseFluent> extends ExternalDatabaseSpecFluent<SpecNestedImpl<N>> implements
            Nested<N> {

        private final N parentBuilder;

        SpecNestedImpl(N parentBuilder, ExternalDatabaseSpec item) {
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
