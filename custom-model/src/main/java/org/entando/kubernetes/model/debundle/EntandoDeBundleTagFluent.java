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

public abstract class EntandoDeBundleTagFluent<N extends EntandoDeBundleTagFluent<N>> {

    private String version;
    private String integrity;
    private String shasum;
    private String tarball;

    public EntandoDeBundleTagFluent(EntandoDeBundleTag tag) {
        this.version = tag.getVersion();
        this.integrity = tag.getIntegrity();
        this.shasum = tag.getShasum();
        this.tarball = tag.getTarball();
    }

    public EntandoDeBundleTagFluent() {
    }

    public EntandoDeBundleTag build() {
        return new EntandoDeBundleTag(version, integrity, shasum, tarball);
    }

    public N withVersion(String version) {
        this.version = version;
        return thisAsN();
    }

    public N withIntegrity(String integrity) {
        this.integrity = integrity;
        return thisAsN();
    }

    public N withShasum(String shasum) {
        this.shasum = shasum;
        return thisAsN();
    }

    public N withTarball(String tarball) {
        this.tarball = tarball;
        return thisAsN();
    }

    @SuppressWarnings("unchecked")
    protected N thisAsN() {
        return (N) this;
    }
}
