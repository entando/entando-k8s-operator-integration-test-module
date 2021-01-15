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

package org.entando.kubernetes.controller.common;

import static java.lang.String.format;

import io.fabric8.kubernetes.api.model.ConfigMap;
import java.util.Optional;
import org.entando.kubernetes.controller.EntandoOperatorConfigBase;
import org.entando.kubernetes.controller.EntandoOperatorConfigProperty;
import org.entando.kubernetes.controller.PropertyResolution;
import org.entando.kubernetes.controller.spi.DefaultDockerImageInfo;

public class EntandoImageResolver {

    private final ConfigMap imageVersionsConfigMap;

    public EntandoImageResolver(ConfigMap imageVersionsConfigMap) {
        this.imageVersionsConfigMap = imageVersionsConfigMap;
    }

    public String determineImageUri(String imageUri) {
        return determineImageUri(new DefaultDockerImageInfo(imageUri));
    }

    public String determineImageUri(DockerImageInfo dockerImageInfo) {
        //The environment variable injected by the OLM takes precedence over everything
        Optional<String> injectedImageUri = EntandoOperatorConfigBase.lookupProperty(relateImage(dockerImageInfo.getRepository()))
                .or(() -> EntandoOperatorConfigBase.lookupProperty(relateImage(dockerImageInfo.getOrganizationAwareRepository())));
        return injectedImageUri.orElse(format("%s/%s/%s:%s",
                determineDockerRegistry(dockerImageInfo),
                determineOrganization(dockerImageInfo),
                dockerImageInfo.getRepository(),
                //Have to append "latest" here to allow for optional version resolution
                determineVersion(dockerImageInfo).orElse("latest")));
    }

    private String relateImage(String repository) {
        return "related.image." + repository;
    }

    private String determineDockerRegistry(DockerImageInfo dockerImageInfo) {
        return new PropertyResolution(this.imageVersionsConfigMap, dockerImageInfo)
                .withOverridingPropertyName(EntandoOperatorConfigProperty.ENTANDO_DOCKER_REGISTRY_OVERRIDE)
                .withConfigMapKey("registry")
                .withProvidedValue(dockerImageInfo.getRegistry().orElse(null))
                .withFallbackPropertyName(EntandoOperatorConfigProperty.ENTANDO_DOCKER_REGISTRY_FALLBACK)
                .withDefaultValue("docker.io").resolvePropertyValue();
    }

    private String determineOrganization(DockerImageInfo dockerImageInfo) {
        return new PropertyResolution(this.imageVersionsConfigMap, dockerImageInfo)
                .withOverridingPropertyName(EntandoOperatorConfigProperty.ENTANDO_DOCKER_IMAGE_ORG_OVERRIDE)
                .withConfigMapKey("organization")
                .withProvidedValue(dockerImageInfo.getOrganization().orElse(null))
                .withFallbackPropertyName(EntandoOperatorConfigProperty.ENTANDO_DOCKER_IMAGE_ORG_FALLBACK)
                .withDefaultValue("entando").resolvePropertyValue();
    }

    private Optional<String> determineVersion(DockerImageInfo dockerImageInfo) {
        //For the version, we allow the dockerImageInfo to override it primarily for testing purposes
        //It is extremely unlikely that any version will be provided for any context other than testing
        return Optional.ofNullable(dockerImageInfo.getVersion().orElse(new PropertyResolution(this.imageVersionsConfigMap, dockerImageInfo)
                .withOverridingPropertyName(EntandoOperatorConfigProperty.ENTANDO_DOCKER_IMAGE_VERSION_OVERRIDE)
                .withConfigMapKey("version")
                .withProvidedValue(null)
                .withFallbackPropertyName(EntandoOperatorConfigProperty.ENTANDO_DOCKER_IMAGE_VERSION_FALLBACK)
                .withDefaultValue(null).resolvePropertyValue()));
    }

}
