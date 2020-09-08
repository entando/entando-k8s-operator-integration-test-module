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

package org.entando.kubernetes.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.entando.kubernetes.model.app.EntandoAppSpecFluent;

public abstract class EntandoDeploymentSpecBuilder<N extends EntandoDeploymentSpecBuilder> {

    protected DbmsVendor dbms;
    protected String ingressHostName;
    protected String tlsSecretName;
    protected Integer replicas = 1;
    protected String serviceAccountToUse;

    protected Map<String, String> parameters;
    protected EntandoResourceRequirements resourceRequirements;

    protected EntandoDeploymentSpecBuilder(EntandoDeploymentSpec spec) {
        this.dbms = spec.getDbms().orElse(null);
        this.ingressHostName = spec.getIngressHostName().orElse(null);
        this.replicas = spec.getReplicas().orElse(null);
        this.tlsSecretName = spec.getTlsSecretName().orElse(null);
        this.serviceAccountToUse = spec.getServiceAccountToUse().orElse(null);
        this.parameters = new ConcurrentHashMap<>(spec.getParameters());
        this.resourceRequirements = spec.getResourceRequirements().orElse(null);
    }

    protected EntandoDeploymentSpecBuilder() {
        this.parameters = new ConcurrentHashMap<>();
    }

    public final N withDbms(DbmsVendor dbms) {
        this.dbms = dbms;
        return thisAsN();
    }

    public final N withTlsSecretName(String tlsSecretName) {
        this.tlsSecretName = tlsSecretName;
        return thisAsN();
    }

    public final N withServiceAccountToUse(String serviceAccountToUse) {
        this.serviceAccountToUse = serviceAccountToUse;
        return thisAsN();
    }

    public final N withReplicas(int replicas) {
        this.replicas = replicas;
        return thisAsN();
    }

    public final N withIngressHostName(String ingressHostName) {
        this.ingressHostName = ingressHostName;
        return thisAsN();
    }

    public N withParameters(Map<String, String> parameters) {
        this.parameters.clear();
        this.parameters.putAll(parameters);
        return thisAsN();
    }

    public N addNewParameter(String name, String value) {
        this.parameters.put(name, value);
        return thisAsN();
    }

    public EntandoResourceRequirementsNested editResourceRequirements() {
        return new EntandoResourceRequirementsNested(thisAsN(), resourceRequirements);
    }

    public EntandoResourceRequirementsNested withNewResourceRequirements() {
        return new EntandoResourceRequirementsNested(thisAsN());
    }

    public N withResourceRequirements(EntandoResourceRequirements resourceRequirements) {
        this.resourceRequirements = resourceRequirements;
        return thisAsN();
    }

    @SuppressWarnings("unchecked")
    protected N thisAsN() {
        return (N) this;
    }

    public class EntandoResourceRequirementsNested extends EntandoResourceRequirementsFluent<EntandoResourceRequirementsNested> {

        private N parent;

        public EntandoResourceRequirementsNested(N parent, EntandoResourceRequirements resourceRequirements) {
            super(resourceRequirements);
            this.parent = parent;
        }

        public EntandoResourceRequirementsNested(N parent) {
            this.parent = parent;
        }

        public N done() {
            this.parent.withResourceRequirements(build());
            return this.parent;
        }

        public N endResourceRequirements() {
            return done();
        }
    }

}
