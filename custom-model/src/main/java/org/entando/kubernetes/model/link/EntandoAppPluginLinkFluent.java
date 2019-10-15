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

package org.entando.kubernetes.model.link;

import io.fabric8.kubernetes.api.builder.BaseFluent;
import io.fabric8.kubernetes.api.builder.Fluent;
import io.fabric8.kubernetes.api.builder.Nested;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.ObjectMetaFluentImpl;

public class EntandoAppPluginLinkFluent<A extends EntandoAppPluginLinkFluent<A>> extends BaseFluent<A> implements Fluent<A> {

    protected ObjectMetaBuilder metadata;
    protected EntandoAppPluginLinkSpecBuilder spec;

    protected EntandoAppPluginLinkFluent() {
        super();
        this.metadata = new ObjectMetaBuilder();
        this.spec = new EntandoAppPluginLinkSpecBuilder();
    }

    protected EntandoAppPluginLinkFluent(EntandoAppPluginLinkSpec spec, ObjectMeta objectMeta) {
        super();
        this.metadata = new ObjectMetaBuilder(objectMeta);
        this.spec = new EntandoAppPluginLinkSpecBuilder(spec);
    }

    public MetadataNestedImpl<A> editMetadata() {
        return new MetadataNestedImpl<A>((A) this, this.metadata.build());
    }

    public MetadataNestedImpl<A> withNewMetadata() {
        return new MetadataNestedImpl<A>((A) this);
    }

    public A withMetadata(ObjectMeta metadata) {
        this.metadata = new ObjectMetaBuilder(metadata);
        return (A) this;
    }

    public SpecNestedImpl<A> editSpec() {
        return new SpecNestedImpl<A>((A) this, this.spec.build());
    }

    public SpecNestedImpl<A> withNewSpec() {
        return new SpecNestedImpl<A>((A) this);
    }

    public A withSpec(EntandoAppPluginLinkSpec spec) {
        this.spec = new EntandoAppPluginLinkSpecBuilder(spec);
        return (A) this;
    }

    public static class SpecNestedImpl<N extends EntandoAppPluginLinkFluent> extends
            EntandoAppPluginLinkSpecBuilder<SpecNestedImpl<N>> implements
            Nested<N> {

        private final N parentBuilder;

        SpecNestedImpl(N parentBuilder, EntandoAppPluginLinkSpec item) {
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

    public static class MetadataNestedImpl<N extends EntandoAppPluginLinkFluent<N>> extends
            ObjectMetaFluentImpl<MetadataNestedImpl<N>> implements
            Nested<N> {

        private final N parentBuilder;
        private final ObjectMetaBuilder builder;

        MetadataNestedImpl(N parentBuilder, ObjectMeta item) {
            super();
            this.parentBuilder = parentBuilder;
            this.builder = new ObjectMetaBuilder(this, item);
        }

        MetadataNestedImpl(N parentBuilder) {
            super();
            this.parentBuilder = parentBuilder;
            this.builder = new ObjectMetaBuilder(this);
        }

        @Override
        public N and() {
            return parentBuilder.withMetadata(this.builder.build());
        }

        public N endMetadata() {
            return this.and();
        }
    }

}
