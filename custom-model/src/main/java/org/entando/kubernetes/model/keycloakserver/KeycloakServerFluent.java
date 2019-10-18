/*
 *
 *  * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
 *  *
 *  * This library is free software; you can redistribute it and/or modify it under
 *  * the terms of the GNU Lesser General Public License as published by the Free
 *  * Software Foundation; either version 2.1 of the License, or (at your option)
 *  * any later version.
 *  *
 *  * This library is distributed in the hope that it will be useful, but WITHOUT
 *  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 *  * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 *  * details.
 *
 */

package org.entando.kubernetes.model.keycloakserver;

import io.fabric8.kubernetes.api.builder.Fluent;
import io.fabric8.kubernetes.api.builder.Nested;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import org.entando.kubernetes.model.EntandoBaseFluent;
import org.entando.kubernetes.model.MetadataNestedImpl;

public class KeycloakServerFluent<A extends KeycloakServerFluent<A>> extends EntandoBaseFluent<A> implements Fluent<A> {

    protected ObjectMetaBuilder metadata;
    protected KeycloakServerSpecBuilder spec;

    protected KeycloakServerFluent() {
        super();
        this.metadata = new ObjectMetaBuilder();
        this.spec = new KeycloakServerSpecBuilder();
    }

    protected KeycloakServerFluent(KeycloakServerSpec spec, ObjectMeta objectMeta) {
        super();
        this.metadata = new ObjectMetaBuilder(objectMeta);
        this.spec = new KeycloakServerSpecBuilder(spec);
    }

    public MetadataNestedImpl<A> editMetadata() {
        return new MetadataNestedImpl<>((A) this, this.metadata.build());
    }

    public MetadataNestedImpl<A> withNewMetadata() {
        return new MetadataNestedImpl<>((A) this);
    }

    @Override
    public A withMetadata(ObjectMeta metadata) {
        this.metadata = new ObjectMetaBuilder(metadata);
        return (A) this;
    }

    public SpecNestedImpl<A> editSpec() {
        return new SpecNestedImpl<>((A) this, this.spec.build());
    }

    public SpecNestedImpl<A> withNewSpec() {
        return new SpecNestedImpl<>((A) this);
    }

    public A withSpec(KeycloakServerSpec spec) {
        this.spec = new KeycloakServerSpecBuilder(spec);
        return (A) this;
    }

    public static class SpecNestedImpl<N extends KeycloakServerFluent> extends
            KeycloakServerSpecBuilder<KeycloakServerFluent.SpecNestedImpl<N>> implements
            Nested<N> {

        private final N parentBuilder;

        SpecNestedImpl(N parentBuilder, KeycloakServerSpec item) {
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
