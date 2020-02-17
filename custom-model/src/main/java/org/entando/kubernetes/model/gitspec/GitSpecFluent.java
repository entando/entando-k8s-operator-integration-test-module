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

package org.entando.kubernetes.model.gitspec;

public abstract class GitSpecFluent<N extends GitSpecFluent> {

    private String repository;
    private String secretName;
    private String targetRef;
    private GitResponsibility responsibility;

    public GitSpecFluent() {
    }

    public GitSpecFluent(GitSpec gitSpec) {
        this.repository = gitSpec.getRepository();
        this.secretName = gitSpec.getSecretName().orElse(null);
        this.targetRef = gitSpec.getTargetRef().orElse(null);
        this.responsibility = gitSpec.getResponsibility();
    }

    public N withRepository(String repository) {
        this.repository = repository;
        return thisAsN();
    }

    public N withSecretName(String secretName) {
        this.secretName = secretName;
        return thisAsN();
    }

    public N withTargertRef(String targetRef) {
        this.targetRef = targetRef;
        return thisAsN();
    }

    public N withResponsibility(GitResponsibility responsibility) {
        this.responsibility = responsibility;
        return thisAsN();
    }

    @SuppressWarnings("unchecked")
    protected N thisAsN() {
        return (N) this;
    }

    public GitSpec build() {
        return new GitSpec(repository, secretName, targetRef, responsibility);
    }

}
