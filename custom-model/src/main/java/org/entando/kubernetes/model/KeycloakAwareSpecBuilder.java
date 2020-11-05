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

public abstract class KeycloakAwareSpecBuilder<N extends KeycloakAwareSpecBuilder> extends EntandoIngressingDeploymentSpecBuilder<N> {

    protected KeycloakToUse keycloakToUse;

    protected KeycloakAwareSpecBuilder(KeycloakAwareSpec spec) {
        super(spec);
        this.keycloakToUse = spec.getKeycloakToUse().orElse(null);
    }

    protected KeycloakAwareSpecBuilder() {
    }

    public N withKeycloakToUse(KeycloakToUse keycloakToUse) {
        this.keycloakToUse = keycloakToUse;
        return thisAsN();
    }

    public KeycloakToUseNested withNewKeycloakToUse() {
        return new KeycloakToUseNested(thisAsN());
    }

    public KeycloakToUseNested editKeycloakToUse() {
        return new KeycloakToUseNested(thisAsN(), keycloakToUse);
    }

    public class KeycloakToUseNested extends
            KeycloakToUseFluent<KeycloakToUseNested> {

        private N parentBuilder;

        public KeycloakToUseNested(N parentBuilder, KeycloakToUse keycloakToUse) {
            super(keycloakToUse);
            this.parentBuilder = parentBuilder;
        }

        public KeycloakToUseNested(N parentBuilder) {
            this.parentBuilder = parentBuilder;
        }

        @SuppressWarnings("unchecked")
        public N endKeycloakToUse() {
            return (N) parentBuilder.withKeycloakToUse(super.build());
        }
    }

}
