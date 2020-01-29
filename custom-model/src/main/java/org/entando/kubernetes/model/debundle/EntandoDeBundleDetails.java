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

import static org.entando.kubernetes.model.Coalescence.coalesce;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@JsonInclude(Include.NON_NULL)
@JsonSerialize
@JsonDeserialize
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
public class EntandoDeBundleDetails implements Serializable {

    private String name;
    private String description;
    @JsonProperty("dist-tags")
    @SuppressWarnings("java:S1948")//because the values will be serializable
    private Map<String, Object> distTags = new ConcurrentHashMap<>();
    private List<String> versions = new ArrayList<>();
    private List<String> keywords = new ArrayList<>();

    public EntandoDeBundleDetails(String name, String description, Map<String, Object> distTags, List<String> versions,
            List<String> keywords) {
        this.name = name;
        this.description = description;
        this.distTags = coalesce(distTags, this.distTags);
        this.versions = coalesce(versions, this.versions);
        this.keywords = coalesce(keywords, this.keywords);
    }

    public EntandoDeBundleDetails() {
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, Object> getDistTags() {
        return distTags;
    }

    public List<String> getVersions() {
        return versions;
    }

    public List<String> getKeywords() {
        return keywords;
    }
}
