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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public abstract class EntandoDeBundleDetailsFluent<N extends EntandoDeBundleDetailsFluent<N>> {

    private String name;
    private String description;
    private Map<String, Object> distTags;
    private List<String> versions;
    private List<String> keywords;
    private String thumbnail;

    public EntandoDeBundleDetailsFluent() {
        this.distTags = new ConcurrentHashMap<>();
        this.versions = new ArrayList<>();
        this.keywords = new ArrayList<>();
    }

    public EntandoDeBundleDetailsFluent(EntandoDeBundleDetails details) {
        this.name = details.getName();
        this.description = details.getDescription();
        this.distTags = Optional.ofNullable(details.getDistTags()).orElse(new ConcurrentHashMap<>());
        this.versions = Optional.ofNullable(details.getVersions()).orElse(new ArrayList<>());
        this.keywords = Optional.ofNullable(details.getKeywords()).orElse(new ArrayList<>());
        this.thumbnail = details.getThumbnail();
    }

    public EntandoDeBundleDetails build() {
        return new EntandoDeBundleDetails(name, description, distTags, versions, keywords, thumbnail);
    }

    public N withName(String name) {
        this.name = name;
        return thisAsN();
    }

    public N withDescription(String description) {
        this.description = description;
        return thisAsN();
    }

    public N withVersions(List<String> versions) {
        this.versions = new ArrayList<>(versions);
        return thisAsN();
    }

    public N addNewVersion(String version) {
        this.versions.add(version);
        return thisAsN();
    }

    public N withKeywords(List<String> keywords) {
        this.keywords = new ArrayList<>(keywords);
        return thisAsN();
    }

    public N addNewKeyword(String keyword) {
        this.keywords.add(keyword);
        return thisAsN();
    }

    public N withThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
        return thisAsN();
    }

    public N withDistTags(Map<String, Object> distTags) {
        this.distTags = new ConcurrentHashMap<>(distTags);
        return thisAsN();
    }

    public N addNewDistTag(String name, Object value) {
        this.distTags.put(name, value);
        return thisAsN();
    }

    @SuppressWarnings("unchecked")
    protected N thisAsN() {
        return (N) this;
    }
}
