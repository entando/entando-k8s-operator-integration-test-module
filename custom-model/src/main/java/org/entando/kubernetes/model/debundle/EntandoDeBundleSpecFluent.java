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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class EntandoDeBundleSpecFluent<A extends EntandoDeBundleSpecFluent> {

    protected EntandoDeBundleDetailsBuilder details;
    protected List<EntandoDeBundleTagBuilder> tags;

    public EntandoDeBundleSpecFluent(EntandoDeBundleSpec spec) {
        this.details = new EntandoDeBundleDetailsBuilder(spec.getDetails());
        this.tags = createTagBuilders(spec.getTags());
    }

    public EntandoDeBundleSpecFluent() {
        this.details = new EntandoDeBundleDetailsBuilder();
        this.tags = new ArrayList<>();
    }

    private List<EntandoDeBundleTagBuilder> createTagBuilders(List<EntandoDeBundleTag> tags) {
        return new ArrayList<>(tags.stream().map(EntandoDeBundleTagBuilder::new).collect(Collectors.toList()));
    }

    public DetailsNested<A> withNewDetails() {
        return new DetailsNested<>(thisAsA());
    }

    public DetailsNested<A> editDetails() {
        return new DetailsNested<>(thisAsA(), details.build());
    }

    public TagNested<A> addNewTag() {
        return new TagNested<>(thisAsA());
    }

    public A addToTags(EntandoDeBundleTag tag) {
        this.tags.add(new EntandoDeBundleTagBuilder(tag));
        return thisAsA();
    }

    @SuppressWarnings("unchecked")
    protected A thisAsA() {
        return (A) this;
    }

    public A withDetails(EntandoDeBundleDetails details) {
        this.details = new EntandoDeBundleDetailsBuilder(details);
        return thisAsA();
    }

    public EntandoDeBundleSpec build() {
        return new EntandoDeBundleSpec(this.details.build(),
                this.tags.stream().map(EntandoDeBundleTagFluent::build).collect(Collectors.toList()));
    }

    public A withTags(List<EntandoDeBundleTag> tags) {
        this.tags = createTagBuilders(tags);
        return thisAsA();
    }

    public static class DetailsNested<N extends EntandoDeBundleSpecFluent> extends
            EntandoDeBundleDetailsFluent<DetailsNested<N>> implements Nested<N> {

        private final N parentBuilder;

        public DetailsNested(N parentBuilder, EntandoDeBundleDetails details) {
            super(details);
            this.parentBuilder = parentBuilder;
        }

        public DetailsNested(N parentBuilder) {
            super();
            this.parentBuilder = parentBuilder;
        }

        @SuppressWarnings("unchecked")
        public N and() {
            return (N) parentBuilder.withDetails(build());
        }

        public N endDetails() {
            return and();
        }
    }

    public static class TagNested<N extends EntandoDeBundleSpecFluent> extends
            EntandoDeBundleTagFluent<TagNested<N>> implements Nested<N> {

        private final N parentBuilder;

        public TagNested(N parentBuilder) {
            super();
            this.parentBuilder = parentBuilder;
        }

        @SuppressWarnings("unchecked")
        public N and() {
            return (N) parentBuilder.addToTags(build());
        }

        public N endTag() {
            return and();
        }
    }

}
