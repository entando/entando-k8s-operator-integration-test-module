package org.entando.kubernetes.model.app;

import io.fabric8.kubernetes.api.builder.BaseFluent;
import io.fabric8.kubernetes.api.builder.Fluent;
import io.fabric8.kubernetes.api.builder.Nested;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.ObjectMetaFluentImpl;
import org.entando.kubernetes.model.app.EntandoAppSpec.EntandoAppSpecBuilder;

public class EntandoAppFluent<A extends EntandoAppFluent<A>> extends BaseFluent<A> implements Fluent<A> {

    protected ObjectMetaBuilder metadata;
    protected EntandoAppSpecBuilder spec;

    protected EntandoAppFluent() {
        super();
        this.metadata = new ObjectMetaBuilder();
        this.spec = new EntandoAppSpecBuilder();
    }

    protected EntandoAppFluent(EntandoAppSpec spec, ObjectMeta objectMeta) {
        super();
        this.metadata = new ObjectMetaBuilder(objectMeta);
        this.spec = new EntandoAppSpecBuilder(spec);
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

    public A withSpec(EntandoAppSpec spec) {
        this.spec = new EntandoAppSpecBuilder(spec);
        return (A) this;
    }

    public static class SpecNestedImpl<N extends EntandoAppFluent> extends EntandoAppSpecBuilder<SpecNestedImpl<N>> implements Nested<N> {

        private final N parentBuilder;

        SpecNestedImpl(N parentBuilder, EntandoAppSpec item) {
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

    public static class MetadataNestedImpl<N extends EntandoAppFluent<N>> extends ObjectMetaFluentImpl<MetadataNestedImpl<N>> implements
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
