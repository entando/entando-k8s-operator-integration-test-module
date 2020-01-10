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

package org.entando.kubernetes.model.compositeapp;

import io.fabric8.kubernetes.api.builder.Builder;
import io.fabric8.kubernetes.api.builder.Nested;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.entando.kubernetes.model.EntandoBaseCustomResource;
import org.entando.kubernetes.model.EntandoBaseFluent;
import org.entando.kubernetes.model.app.EntandoApp;
import org.entando.kubernetes.model.app.EntandoAppBuilder;
import org.entando.kubernetes.model.app.EntandoAppFluent;
import org.entando.kubernetes.model.debundle.EntandoDeBundle;
import org.entando.kubernetes.model.debundle.EntandoDeBundleBuilder;
import org.entando.kubernetes.model.externaldatabase.EntandoDatabaseService;
import org.entando.kubernetes.model.externaldatabase.EntandoDatabaseServiceBuilder;
import org.entando.kubernetes.model.externaldatabase.EntandoDatabaseServiceFluent;
import org.entando.kubernetes.model.infrastructure.EntandoClusterInfrastructure;
import org.entando.kubernetes.model.infrastructure.EntandoClusterInfrastructureBuilder;
import org.entando.kubernetes.model.infrastructure.EntandoClusterInfrastructureFluent;
import org.entando.kubernetes.model.keycloakserver.EntandoKeycloakServer;
import org.entando.kubernetes.model.keycloakserver.EntandoKeycloakServerBuilder;
import org.entando.kubernetes.model.keycloakserver.EntandoKeycloakServerFluent;
import org.entando.kubernetes.model.link.EntandoAppPluginLink;
import org.entando.kubernetes.model.link.EntandoAppPluginLinkBuilder;
import org.entando.kubernetes.model.link.EntandoAppPluginLinkFluent;
import org.entando.kubernetes.model.plugin.EntandoPlugin;
import org.entando.kubernetes.model.plugin.EntandoPluginBuilder;
import org.entando.kubernetes.model.plugin.EntandoPluginFluent;

public abstract class EntandoCompositeAppSpecFluent<A extends EntandoCompositeAppSpecFluent> {

    private static final Map<Class<? extends EntandoBaseCustomResource>, Class<? extends EntandoBaseFluent>> BUILDERS = createBuilderMap();
    protected List<EntandoBaseFluent<?>> components;

    public EntandoCompositeAppSpecFluent(EntandoCompositeAppSpec spec) {
        this.components = createComponentBuilders(spec.getComponents());
    }

    public EntandoCompositeAppSpecFluent() {
        this.components = new ArrayList<>();
    }

    private static ConcurrentHashMap<Class<? extends EntandoBaseCustomResource>, Class<? extends EntandoBaseFluent>> createBuilderMap() {
        ConcurrentHashMap<Class<? extends EntandoBaseCustomResource>, Class<? extends EntandoBaseFluent>> result =
                new ConcurrentHashMap<>();
        result.put(EntandoKeycloakServer.class, EntandoKeycloakServerBuilder.class);
        result.put(EntandoClusterInfrastructure.class, EntandoClusterInfrastructureBuilder.class);
        result.put(EntandoApp.class, EntandoAppBuilder.class);
        result.put(EntandoPlugin.class, EntandoPluginBuilder.class);
        result.put(EntandoAppPluginLink.class, EntandoAppPluginLinkBuilder.class);
        result.put(EntandoDatabaseService.class, EntandoDatabaseServiceBuilder.class);
        result.put(EntandoDeBundle.class, EntandoDeBundleBuilder.class);
        return result;
    }

    public A withComponents(List<EntandoBaseCustomResource> components) {
        this.components = createComponentBuilders(components);
        return thisAsA();
    }

    public A withComponents(EntandoBaseCustomResource... components) {
        return withComponents(Arrays.asList(components));
    }

    private List<EntandoBaseFluent<?>> createComponentBuilders(List<EntandoBaseCustomResource> components) {
        return new ArrayList<>(components.stream()
                .map(this::newBuilderFrom)
                .collect(Collectors.toList()));
    }

    private EntandoBaseFluent<?> newBuilderFrom(EntandoBaseCustomResource r) {
        try {
            return (EntandoBaseFluent<?>) BUILDERS.get(r.getClass()).getConstructor(r.getClass()).newInstance(r);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("unchecked")
    protected A thisAsA() {
        return (A) this;
    }

    public EntandoCompositeAppSpec build() {
        return new EntandoCompositeAppSpec(this.components.stream()
                .map(Builder.class::cast)
                .map(Builder::build)
                .map(EntandoBaseCustomResource.class::cast)
                .collect(Collectors.toList()));
    }

    public EntandoKeycloakServerNested<A> addNewEntandoKeycloakServer() {
        return new EntandoKeycloakServerNested<>(thisAsA());
    }

    public A addToEntandoKeycloakServers(EntandoKeycloakServer item) {
        this.components.add(new EntandoKeycloakServerBuilder(item));
        return thisAsA();
    }

    public EntandoAppNested<A> addNewEntandoApp() {
        return new EntandoAppNested<>(thisAsA());
    }

    public A addToEntandoApps(EntandoApp item) {
        this.components.add(new EntandoAppBuilder(item));
        return thisAsA();
    }

    public EntandoClusterInfrastructureNested<A> addNewEntandoClusterInfrastructure() {
        return new EntandoClusterInfrastructureNested<>(thisAsA());
    }

    public A addToEntandoClusterInfrastructures(EntandoClusterInfrastructure item) {
        this.components.add(new EntandoClusterInfrastructureBuilder(item));
        return thisAsA();
    }

    public EntandoPluginNested<A> addNewEntandoPlugin() {
        return new EntandoPluginNested<>(thisAsA());
    }

    public A addToEntandoPlugins(EntandoPlugin item) {
        this.components.add(new EntandoPluginBuilder(item));
        return thisAsA();
    }

    public EntandoAppPluginLinkNested<A> addNewEntandoAppPluginLink() {
        return new EntandoAppPluginLinkNested<>(thisAsA());
    }

    public A addToEntandoAppPluginLinks(EntandoAppPluginLink item) {
        this.components.add(new EntandoAppPluginLinkBuilder(item));
        return thisAsA();
    }

    public EntandoDatabaseServiceNested<A> addNewEntandoDatabaseService() {
        return new EntandoDatabaseServiceNested<>(thisAsA());
    }

    public A addToEntandoDatabaseServices(EntandoDatabaseService item) {
        this.components.add(new EntandoDatabaseServiceBuilder(item));
        return thisAsA();
    }

    public static class EntandoKeycloakServerNested<N extends EntandoCompositeAppSpecFluent> extends
            EntandoKeycloakServerFluent<EntandoKeycloakServerNested<N>> implements Nested<N> {

        private final N parentBuilder;

        public EntandoKeycloakServerNested(N parentBuilder) {
            super();
            this.parentBuilder = parentBuilder;
        }

        @SuppressWarnings("unchecked")
        public N and() {
            return (N) parentBuilder.addToEntandoKeycloakServers(new EntandoKeycloakServer(super.metadata.build(), super.spec.build()));
        }

        public N endEntandoKeycloakServer() {
            return and();
        }
    }

    public static class EntandoAppNested<N extends EntandoCompositeAppSpecFluent> extends
            EntandoAppFluent<EntandoAppNested<N>> implements Nested<N> {

        private final N parentBuilder;

        public EntandoAppNested(N parentBuilder) {
            super();
            this.parentBuilder = parentBuilder;
        }

        @SuppressWarnings("unchecked")
        public N and() {
            return (N) parentBuilder
                    .addToEntandoApps(new EntandoApp(super.metadata.build(), super.spec.build()));
        }

        public N endEntandoApp() {
            return and();
        }
    }

    public static class EntandoClusterInfrastructureNested<N extends EntandoCompositeAppSpecFluent> extends
            EntandoClusterInfrastructureFluent<EntandoClusterInfrastructureNested<N>> implements Nested<N> {

        private final N parentBuilder;

        public EntandoClusterInfrastructureNested(N parentBuilder) {
            super();
            this.parentBuilder = parentBuilder;
        }

        @SuppressWarnings("unchecked")
        public N and() {
            return (N) parentBuilder
                    .addToEntandoClusterInfrastructures(new EntandoClusterInfrastructure(super.metadata.build(), super.spec.build()));
        }

        public N endEntandoClusterInfrastructure() {
            return and();
        }
    }

    public static class EntandoPluginNested<N extends EntandoCompositeAppSpecFluent> extends
            EntandoPluginFluent<EntandoPluginNested<N>> implements Nested<N> {

        private final N parentBuilder;

        public EntandoPluginNested(N parentBuilder) {
            super();
            this.parentBuilder = parentBuilder;
        }

        @SuppressWarnings("unchecked")
        public N and() {
            return (N) parentBuilder
                    .addToEntandoPlugins(new EntandoPlugin(super.metadata.build(), super.spec.build()));
        }

        public N endEntandoPlugin() {
            return and();
        }
    }

    public static class EntandoAppPluginLinkNested<N extends EntandoCompositeAppSpecFluent> extends
            EntandoAppPluginLinkFluent<EntandoAppPluginLinkNested<N>> implements Nested<N> {

        private final N parentBuilder;

        public EntandoAppPluginLinkNested(N parentBuilder) {
            super();
            this.parentBuilder = parentBuilder;
        }

        @SuppressWarnings("unchecked")
        public N and() {
            return (N) parentBuilder
                    .addToEntandoAppPluginLinks(new EntandoAppPluginLink(super.metadata.build(), super.spec.build()));
        }

        public N endEntandoAppPluginLink() {
            return and();
        }
    }

    public static class EntandoDatabaseServiceNested<N extends EntandoCompositeAppSpecFluent> extends
            EntandoDatabaseServiceFluent<EntandoDatabaseServiceNested<N>> implements Nested<N> {

        private final N parentBuilder;

        public EntandoDatabaseServiceNested(N parentBuilder) {
            super();
            this.parentBuilder = parentBuilder;
        }

        @SuppressWarnings("unchecked")
        public N and() {
            return (N) parentBuilder
                    .addToEntandoDatabaseServices(new EntandoDatabaseService(super.metadata.build(), super.spec.build()));
        }

        public N endEntandoDatabaseService() {
            return and();
        }
    }

}
