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

package org.entando.kubernetes.model.common;

import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@JsonSerialize
@JsonDeserialize
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServerStatus implements Serializable {

    private final String type = "WebServerStatus";//for backward compatibility
    private String qualifier;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "GMT")
    private Date started = new Date();
    private CustomResourceReference originatingCustomResource;
    private ResourceReference originatingControllerPod;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "GMT")
    private Date finished;
    private String serviceName;
    private String deploymentName;
    private String adminSecretName;
    private String ssoRealm;
    private String ssoClientId;
    private Map<String, String> webContexts;
    private Map<String, String> podPhases;
    private Map<String, String> persistentVolumeClaimPhases;
    private Map<String, String> derivedDeploymentParameters;
    private EntandoControllerFailure entandoControllerFailure;
    private String ingressName;
    private String externalBaseUrl;

    protected ServerStatus() {
        //For json deserialization only. Qualifier is requried
    }

    public ServerStatus(String qualifier) {
        this.qualifier = qualifier;
    }

    public ServerStatus(String newQualifier, ServerStatus original) {
        this.qualifier = newQualifier;
        this.started = original.started;
        this.originatingCustomResource = original.originatingCustomResource;
        this.originatingControllerPod = original.originatingControllerPod;
        this.finished = original.finished;
        this.serviceName = original.serviceName;
        this.deploymentName = original.deploymentName;
        this.adminSecretName = original.adminSecretName;
        this.podPhases = ofNullable(original.podPhases).map(HashMap::new).orElse(null);
        this.persistentVolumeClaimPhases = ofNullable(original.persistentVolumeClaimPhases).map(HashMap::new).orElse(null);
        this.derivedDeploymentParameters = ofNullable(original.derivedDeploymentParameters).map(HashMap::new).orElse(null);
        this.webContexts = ofNullable(original.webContexts).map(HashMap::new).orElse(null);
        this.entandoControllerFailure = original.entandoControllerFailure;
        this.ingressName = original.ingressName;
        this.externalBaseUrl = original.externalBaseUrl;
        this.ssoClientId = original.ssoClientId;
        this.ssoRealm = original.ssoRealm;
    }

    public String getQualifier() {
        return qualifier;
    }

    public ResourceReference getOriginatingCustomResource() {
        return originatingCustomResource;
    }

    public ServerStatus withOriginatingCustomResource(HasMetadata originatingCustomResource) {
        this.originatingCustomResource = new CustomResourceReference(originatingCustomResource.getApiVersion(),
                originatingCustomResource.getKind(),
                originatingCustomResource.getMetadata().getNamespace(),
                originatingCustomResource.getMetadata().getName());
        return this;
    }

    public ResourceReference getOriginatingControllerPod() {
        return originatingControllerPod;
    }

    public ServerStatus withOriginatingControllerPod(String namespace, String name) {
        this.originatingControllerPod = new ResourceReference(namespace, name);
        return this;
    }

    public void finish() {
        this.finished = new Date();
    }

    public Date getStarted() {
        return started;
    }

    public Optional<Date> getFinished() {
        return ofNullable(finished);
    }

    public Optional<EntandoControllerFailure> getEntandoControllerFailure() {
        return ofNullable(entandoControllerFailure);
    }

    public void setEntandoControllerFailure(EntandoControllerFailure entandoControllerFailure) {
        this.entandoControllerFailure = entandoControllerFailure;
    }

    public boolean hasFailed() {
        return entandoControllerFailure != null;
    }

    public Optional<String> getServiceName() {
        return ofNullable(serviceName);
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Optional<String> getDeploymentName() {
        return ofNullable(deploymentName);
    }

    public void setDeploymentName(String deploymentName) {
        this.deploymentName = deploymentName;
    }

    public Map<String, String> getDerivedDeploymentParameters() {
        return ofNullable(derivedDeploymentParameters).map(Collections::unmodifiableMap).orElseGet(Collections::emptyMap);
    }

    public void putDerivedDeploymentParameter(String parameterName, String parameterValue) {
        this.derivedDeploymentParameters = Objects.requireNonNullElseGet(this.derivedDeploymentParameters, HashMap::new);
        derivedDeploymentParameters.put(parameterName, parameterValue);
    }

    public Map<String, String> getPersistentVolumeClaimPhases() {
        return ofNullable(persistentVolumeClaimPhases).map(Collections::unmodifiableMap).orElseGet(Collections::emptyMap);
    }

    public void putPersistentVolumeClaimPhase(String pvcName, String pvcPhase) {
        this.persistentVolumeClaimPhases = Objects.requireNonNullElseGet(this.persistentVolumeClaimPhases, HashMap::new);
        persistentVolumeClaimPhases.put(pvcName, pvcPhase);
    }

    public Map<String, String> getPodPhases() {
        return ofNullable(podPhases).map(Collections::unmodifiableMap).orElseGet(Collections::emptyMap);
    }

    public void putPodPhase(String podName, String podPhase) {
        this.podPhases = Objects.requireNonNullElseGet(this.podPhases, HashMap::new);
        podPhases.put(podName, podPhase);
    }

    public void putWebContext(String qualifier, String path) {
        this.webContexts = Objects.requireNonNullElseGet(this.webContexts, HashMap::new);
        webContexts.put(qualifier, path);
    }

    public ServerStatus addToWebContexts(String qualifier, String path) {
        this.putWebContext(qualifier, path);
        return this;
    }

    public Map<String, String> getWebContexts() {
        return ofNullable(webContexts).map(Collections::unmodifiableMap).orElseGet(Collections::emptyMap);
    }

    public void finishWith(EntandoControllerFailure failure) {
        finish();
        setEntandoControllerFailure(failure);
    }

    public Optional<String> getIngressName() {
        return Optional.ofNullable(ingressName);
    }

    public void setExternalBaseUrl(String externalBaseUrl) {
        this.externalBaseUrl = externalBaseUrl;
    }

    public Optional<String> getExternalBaseUrl() {
        return Optional.ofNullable(externalBaseUrl);
    }

    public void setIngressName(String ingressName) {
        this.ingressName = ingressName;
    }

    public Optional<String> getAdminSecretName() {
        return Optional.ofNullable(adminSecretName);
    }

    public void setAdminSecretName(String adminSecretName) {
        this.adminSecretName = adminSecretName;
    }

    public Optional<String> getSsoRealm() {
        return Optional.ofNullable(ssoRealm);
    }

    public void setSsoRealm(String ssoRealm) {
        this.ssoRealm = ssoRealm;
    }

    public ServerStatus withSsoRealm(String ssoRealm) {
        this.setSsoRealm(ssoRealm);
        return this;
    }

    public Optional<String> getSsoClientId() {
        return Optional.ofNullable(ssoClientId);
    }

    public void setSsoClientId(String ssoClientId) {
        this.ssoClientId = ssoClientId;
    }

    public ServerStatus withSsoClientId(String ssoClientId) {
        this.setSsoClientId(ssoClientId);
        return this;
    }

    public String getType() {
        return type;
    }
}
