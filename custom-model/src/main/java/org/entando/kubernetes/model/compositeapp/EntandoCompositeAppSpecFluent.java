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
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.entando.kubernetes.model.DbmsVendor;
import org.entando.kubernetes.model.EntandoBaseCustomResource;
import org.entando.kubernetes.model.EntandoFluent;
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

//Sonar gets confused with generics within generics in return types
@SuppressWarnings("java:S1452")
public abstract class EntandoCompositeAppSpecFluent<F extends EntandoCompositeAppSpecFluent<F>> {

    private static final Map<Class<? extends EntandoBaseCustomResource<? extends Serializable>>,
            Class<? extends EntandoFluent<?>>> BUILDERS = createBuilderMap();
    protected List<EntandoFluent<?>> components;
    private String ingressHostNameOverride;
    private DbmsVendor dbmsOverride;
    private String tlsSecretNameOverride;

    protected EntandoCompositeAppSpecFluent(EntandoCompositeAppSpec spec) {
        this.components = createComponentBuilders(spec.getComponents());
        this.ingressHostNameOverride = spec.getIngressHostNameOverride().orElse(null);
        this.dbmsOverride = spec.getDbmsOverride().orElse(null);
        this.tlsSecretNameOverride = spec.getTlsSecretNameOverride().orElse(null);
    }

    protected EntandoCompositeAppSpecFluent() {
        this.components = new ArrayList<>();
    }

    public static EntandoFluent<?> newBuilderFrom(EntandoBaseCustomResource<? extends Serializable> r) {
        try {
            return BUILDERS.get(r.getClass()).getConstructor(r.getClass()).newInstance(r);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Map<Class<? extends EntandoBaseCustomResource<? extends Serializable>>,
            Class<? extends EntandoFluent<?>>> createBuilderMap() {
        Map<Class<? extends EntandoBaseCustomResource<? extends Serializable>>,
                Class<? extends EntandoFluent<?>>> result = new ConcurrentHashMap<>();
        result.put(EntandoKeycloakServer.class, EntandoKeycloakServerBuilder.class);
        result.put(EntandoClusterInfrastructure.class, EntandoClusterInfrastructureBuilder.class);
        result.put(EntandoApp.class, EntandoAppBuilder.class);
        result.put(EntandoPlugin.class, EntandoPluginBuilder.class);
        result.put(EntandoAppPluginLink.class, EntandoAppPluginLinkBuilder.class);
        result.put(EntandoDatabaseService.class, EntandoDatabaseServiceBuilder.class);
        result.put(EntandoDeBundle.class, EntandoDeBundleBuilder.class);
        result.put(EntandoCustomResourceReference.class, EntandoCustomResourceReferenceBuilder.class);
        return result;
    }

    public final F withDbmsOverride(DbmsVendor dbmsOverride) {
        this.dbmsOverride = dbmsOverride;
        return thisAsF();
    }

    public final F withTlsSecretNameOverride(String tlsSecretNameOverride) {
        this.tlsSecretNameOverride = tlsSecretNameOverride;
        return thisAsF();
    }

    public final F withIngressHostNameOverride(String ingressHostNameOverride) {
        this.ingressHostNameOverride = ingressHostNameOverride;
        return thisAsF();
    }

    public F withComponents(List<EntandoBaseCustomResource<? extends Serializable>> components) {
        this.components = createComponentBuilders(components);
        return thisAsF();
    }

    @SafeVarargs
    public final F withComponents(EntandoBaseCustomResource<? extends Serializable>... components) {
        return withComponents(Arrays.asList(components));
    }

    private List<EntandoFluent<?>> createComponentBuilders(List<EntandoBaseCustomResource<? extends Serializable>> components) {
        return components.stream()
                .map(EntandoCompositeAppSpecFluent::newBuilderFrom).collect(Collectors.<EntandoFluent<?>>toList());
    }

    @SuppressWarnings("unchecked")
    protected F thisAsF() {
        return (F) this;
    }

    //Sonar's solution gives compilation errors
    @SuppressWarnings("java:S1612")
    public EntandoCompositeAppSpec build() {
        return new EntandoCompositeAppSpec(this.components.stream()
                .map(Builder.class::cast)
                .map(Builder::build)
                .map(o -> (EntandoBaseCustomResource<?>) o)
                .collect(Collectors.toList()), ingressHostNameOverride, dbmsOverride, tlsSecretNameOverride);
    }

    public EntandoKeycloakServerNested<F> addNewEntandoKeycloakServer() {
        return new EntandoKeycloakServerNested<>(thisAsF());
    }

    public F addToEntandoKeycloakServers(EntandoKeycloakServer item) {
        this.components.add(new EntandoKeycloakServerBuilder(item));
        return thisAsF();
    }

    public EntandoAppNested<F> addNewEntandoApp() {
        return new EntandoAppNested<>(thisAsF());
    }

    public F addToEntandoApps(EntandoApp item) {
        this.components.add(new EntandoAppBuilder(item));
        return thisAsF();
    }

    public EntandoClusterInfrastructureNested<F> addNewEntandoClusterInfrastructure() {
        return new EntandoClusterInfrastructureNested<>(thisAsF());
    }

    public F addToEntandoClusterInfrastructures(EntandoClusterInfrastructure item) {
        this.components.add(new EntandoClusterInfrastructureBuilder(item));
        return thisAsF();
    }

    public EntandoPluginNested<F> addNewEntandoPlugin() {
        return new EntandoPluginNested<>(thisAsF());
    }

    public F addToEntandoPlugins(EntandoPlugin item) {
        this.components.add(new EntandoPluginBuilder(item));
        return thisAsF();
    }

    public EntandoCustomResourceReferenceNested<F> addNewEntandoCustomResourceReference() {
        return new EntandoCustomResourceReferenceNested<>(thisAsF());
    }

    public F addToEntandoCustomResourceReferences(EntandoCustomResourceReference item) {
        this.components.add(new EntandoCustomResourceReferenceBuilder(item));
        return thisAsF();
    }

    public EntandoAppPluginLinkNested<F> addNewEntandoAppPluginLink() {
        return new EntandoAppPluginLinkNested<>(thisAsF());
    }

    public F addToEntandoAppPluginLinks(EntandoAppPluginLink item) {
        this.components.add(new EntandoAppPluginLinkBuilder(item));
        return thisAsF();
    }

    public EntandoDatabaseServiceNested<F> addNewEntandoDatabaseService() {
        return new EntandoDatabaseServiceNested<>(thisAsF());
    }

    public F addToEntandoDatabaseServices(EntandoDatabaseService item) {
        this.components.add(new EntandoDatabaseServiceBuilder(item));
        return thisAsF();
    }

    public static class EntandoKeycloakServerNested<N extends EntandoCompositeAppSpecFluent<N>> extends
            EntandoKeycloakServerFluent<EntandoKeycloakServerNested<N>> implements Nested<N> {

        private final N parentBuilder;

        public EntandoKeycloakServerNested(N parentBuilder) {
            super();
            this.parentBuilder = parentBuilder;
        }

        public N and() {
            return parentBuilder.addToEntandoKeycloakServers(new EntandoKeycloakServer(super.metadata.build(), super.spec.build()));
        }

        public N endEntandoKeycloakServer() {
            return and();
        }
    }

    public static class EntandoAppNested<N extends EntandoCompositeAppSpecFluent<N>> extends
            EntandoAppFluent<EntandoAppNested<N>> implements Nested<N> {

        private final N parentBuilder;

        public EntandoAppNested(N parentBuilder) {
            super();
            this.parentBuilder = parentBuilder;
        }

        public N and() {
            return parentBuilder
                    .addToEntandoApps(new EntandoApp(super.metadata.build(), super.spec.build()));
        }

        public N endEntandoApp() {
            return and();
        }
    }

    public static class EntandoClusterInfrastructureNested<N extends EntandoCompositeAppSpecFluent<N>> extends
            EntandoClusterInfrastructureFluent<EntandoClusterInfrastructureNested<N>> implements Nested<N> {

        private final N parentBuilder;

        public EntandoClusterInfrastructureNested(N parentBuilder) {
            super();
            this.parentBuilder = parentBuilder;
        }

        public N and() {
            return parentBuilder
                    .addToEntandoClusterInfrastructures(new EntandoClusterInfrastructure(super.metadata.build(), super.spec.build()));
        }

        public N endEntandoClusterInfrastructure() {
            return and();
        }
    }

    public static class EntandoPluginNested<N extends EntandoCompositeAppSpecFluent<N>> extends
            EntandoPluginFluent<EntandoPluginNested<N>> implements Nested<N> {

        private final N parentBuilder;

        public EntandoPluginNested(N parentBuilder) {
            super();
            this.parentBuilder = parentBuilder;
        }

        public N and() {
            return parentBuilder
                    .addToEntandoPlugins(new EntandoPlugin(super.metadata.build(), super.spec.build()));
        }

        public N endEntandoPlugin() {
            return and();
        }
    }

    public static class EntandoAppPluginLinkNested<N extends EntandoCompositeAppSpecFluent<N>> extends
            EntandoAppPluginLinkFluent<EntandoAppPluginLinkNested<N>> implements Nested<N> {

        private final N parentBuilder;

        public EntandoAppPluginLinkNested(N parentBuilder) {
            super();
            this.parentBuilder = parentBuilder;
        }

        public N and() {
            return parentBuilder
                    .addToEntandoAppPluginLinks(new EntandoAppPluginLink(super.metadata.build(), super.spec.build()));
        }

        public N endEntandoAppPluginLink() {
            return and();
        }
    }

    public static class EntandoDatabaseServiceNested<N extends EntandoCompositeAppSpecFluent<N>> extends
            EntandoDatabaseServiceFluent<EntandoDatabaseServiceNested<N>> implements Nested<N> {

        private final N parentBuilder;

        public EntandoDatabaseServiceNested(N parentBuilder) {
            super();
            this.parentBuilder = parentBuilder;
        }

        public N and() {
            return parentBuilder
                    .addToEntandoDatabaseServices(new EntandoDatabaseService(super.metadata.build(), super.spec.build()));
        }

        public N endEntandoDatabaseService() {
            return and();
        }
    }

    public static class EntandoCustomResourceReferenceNested<N extends EntandoCompositeAppSpecFluent<N>> extends
            EntandoCustomResourceReferenceFluent<EntandoCustomResourceReferenceNested<N>> implements Nested<N> {

        private final N parentBuilder;

        public EntandoCustomResourceReferenceNested(N parentBuilder) {
            super();
            this.parentBuilder = parentBuilder;
        }

        public N and() {
            return parentBuilder
                    .addToEntandoCustomResourceReferences(new EntandoCustomResourceReference(super.metadata.build(), super.spec.build()));
        }

        public N endEntandoCustomResourceReference() {
            return and();
        }
    }

}
