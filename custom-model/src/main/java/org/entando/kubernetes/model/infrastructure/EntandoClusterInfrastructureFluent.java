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

package org.entando.kubernetes.model.infrastructure;

import io.fabric8.kubernetes.api.builder.BaseFluent;
import io.fabric8.kubernetes.api.builder.Fluent;
import io.fabric8.kubernetes.api.builder.Nested;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.ObjectMetaFluentImpl;

public class EntandoClusterInfrastructureFluent<A extends EntandoClusterInfrastructureFluent<A>> extends BaseFluent<A> implements
        Fluent<A> {

    protected ObjectMetaBuilder metadata;
    protected EntandoClusterInfrastructureSpecBuilder spec;

    protected EntandoClusterInfrastructureFluent() {
        super();
        this.metadata = new ObjectMetaBuilder();
        this.spec = new EntandoClusterInfrastructureSpecBuilder();
    }

    protected EntandoClusterInfrastructureFluent(EntandoClusterInfrastructureSpec spec, ObjectMeta objectMeta) {
        super();
        this.metadata = new ObjectMetaBuilder(objectMeta);
        this.spec = new EntandoClusterInfrastructureSpecBuilder(spec);
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

    public SpecNestedImplCluster<A> editSpec() {
        return new SpecNestedImplCluster<>((A) this, this.spec.build());
    }

    public SpecNestedImplCluster<A> withNewSpec() {
        return new SpecNestedImplCluster<>((A) this);
    }

    public A withSpec(EntandoClusterInfrastructureSpec spec) {
        this.spec = new EntandoClusterInfrastructureSpecBuilder(spec);
        return (A) this;
    }

    public static class SpecNestedImplCluster<N extends EntandoClusterInfrastructureFluent> extends
            EntandoClusterInfrastructureSpecBuilder<SpecNestedImplCluster<N>> implements
            Nested<N> {

        private final N parentBuilder;

        SpecNestedImplCluster(N parentBuilder, EntandoClusterInfrastructureSpec item) {
            super(item);
            this.parentBuilder = parentBuilder;
        }

        public SpecNestedImplCluster(N parentBuilder) {
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

    public static class MetadataNestedImpl<N extends EntandoClusterInfrastructureFluent<N>> extends
            ObjectMetaFluentImpl<EntandoClusterInfrastructureFluent.MetadataNestedImpl<N>> implements
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
