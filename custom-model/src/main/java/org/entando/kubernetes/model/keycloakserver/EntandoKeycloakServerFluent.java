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

package org.entando.kubernetes.model.keycloakserver;

import io.fabric8.kubernetes.api.builder.Fluent;
import io.fabric8.kubernetes.api.builder.Nested;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import org.entando.kubernetes.model.EntandoBaseFluent;

public class EntandoKeycloakServerFluent<A extends EntandoKeycloakServerFluent<A>> extends EntandoBaseFluent<A> implements Fluent<A> {

    protected EntandoKeycloakServerSpecBuilder spec;

    protected EntandoKeycloakServerFluent() {
        this(new ObjectMetaBuilder(), new EntandoKeycloakServerSpecBuilder());
    }

    protected EntandoKeycloakServerFluent(EntandoKeycloakServerSpec spec, ObjectMeta objectMeta) {
        this(new ObjectMetaBuilder(objectMeta), new EntandoKeycloakServerSpecBuilder(spec));
    }

    private EntandoKeycloakServerFluent(ObjectMetaBuilder metadata, EntandoKeycloakServerSpecBuilder spec) {
        super(metadata);
        this.spec = spec;
    }

    @SuppressWarnings("unchecked")
    public NestedEntandoKeycloakServerFluent<A> editSpec() {
        return new NestedEntandoKeycloakServerFluent<>((A) this, this.spec.build());
    }

    @SuppressWarnings("unchecked")
    public NestedEntandoKeycloakServerFluent<A> withNewSpec() {
        return new NestedEntandoKeycloakServerFluent<>((A) this);
    }

    @SuppressWarnings("unchecked")
    public A withSpec(EntandoKeycloakServerSpec spec) {
        this.spec = new EntandoKeycloakServerSpecBuilder(spec);
        return (A) this;
    }

    public static class NestedEntandoKeycloakServerFluent<N extends EntandoKeycloakServerFluent> extends
            EntandoKeycloakServerSpecFluent<NestedEntandoKeycloakServerFluent<N>> implements
            Nested<N> {

        private final N parentBuilder;

        NestedEntandoKeycloakServerFluent(N parentBuilder, EntandoKeycloakServerSpec item) {
            super(item);
            this.parentBuilder = parentBuilder;
        }

        NestedEntandoKeycloakServerFluent(N parentBuilder) {
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
