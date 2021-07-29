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

package org.entando.kubernetes.model.app;

import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.List;
import java.util.Optional;
import org.entando.kubernetes.model.common.DbmsVendor;
import org.entando.kubernetes.model.common.EntandoResourceRequirements;
import org.entando.kubernetes.model.common.JeeServer;
import org.entando.kubernetes.model.common.KeycloakAwareSpec;
import org.entando.kubernetes.model.common.KeycloakToUse;

@JsonSerialize
@JsonDeserialize()
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
public class EntandoAppSpec extends KeycloakAwareSpec {

    private JeeServer standardServerImage;
    private String customServerImage;
    private String ingressPath;
    private String ecrGitSshSecretName;
    private String entandoAppVersion;

    public EntandoAppSpec() {
        super();
    }

    /**
     * Only for use from the builder.
     */
    @JsonCreator
    public EntandoAppSpec(@JsonProperty("standardServerImage") JeeServer standardServerImage,
            @JsonProperty("customServerImage") String customServerImage,
            @JsonProperty("dbms") DbmsVendor dbms,
            @JsonProperty("ingressHostName") String ingressHostName,
            @JsonProperty("ingressPath") String ingressPath,
            @JsonProperty("replicas") Integer replicas,
            @JsonProperty("tlsSecretName") String tlsSecretName,
            @JsonProperty("keycloakToUse") KeycloakToUse keycloakToUse,
            @JsonProperty("serviceAccountToUse") String serviceAccountToUse,
            @JsonProperty("environmentVariables") List<EnvVar> environmentVariables,
            @JsonProperty("resourceRequirements") EntandoResourceRequirements resourceRequirements,
            @JsonProperty("ecrGitSshSecretName") String ecrGitSshSecretName,
            @JsonProperty("storageClass") String storageClass,
            @JsonProperty("entandoAppVersion") String entandoAppVersion) {
        super(ingressHostName, tlsSecretName, replicas, dbms, serviceAccountToUse, environmentVariables, resourceRequirements,
                keycloakToUse, storageClass);
        this.standardServerImage = standardServerImage;
        this.customServerImage = customServerImage;
        this.ingressPath = ingressPath;
        this.ecrGitSshSecretName = ecrGitSshSecretName;
        this.entandoAppVersion = entandoAppVersion;
    }

    public Optional<String> getEntandoAppVersion() {
        return Optional.ofNullable(entandoAppVersion);
    }

    public Optional<String> getEcrGitSshSecretName() {
        return ofNullable(ecrGitSshSecretName);
    }

    public Optional<String> getIngressPath() {
        return ofNullable(ingressPath);
    }

    public Optional<JeeServer> getStandardServerImage() {
        return ofNullable(standardServerImage);
    }

    public Optional<String> getCustomServerImage() {
        return ofNullable(customServerImage);
    }

}
