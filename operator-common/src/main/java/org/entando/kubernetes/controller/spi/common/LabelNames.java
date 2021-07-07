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

package org.entando.kubernetes.controller.spi.common;

public enum LabelNames {
    RESOURCE_KIND("EntandoResourceKind"),
    RESOURCE_NAMESPACE("EntandoResourceNamespace"),
    JOB_KIND("jobKind"),
    DEPLOYMENT_QUALIFIER("deploymentQualifier"),
    DEPLOYMENT("deployment"),
    CAPABILITY("entando.org/capability"),
    CAPABILITY_IMPLEMENTATION("entando.org/capability-implementation"),
    CAPABILITY_PROVISION_SCOPE("capabilityProvisionScope"),
    CRD_OF_INTEREST("entando.org/crd-of-interest"),
    JOB_KIND_DB_PREPARATION("db-preparation-job");

    private final String labelName;

    LabelNames(String labelName) {
        this.labelName = labelName;
    }

    public java.lang.String getName() {
        return labelName;
    }
}
