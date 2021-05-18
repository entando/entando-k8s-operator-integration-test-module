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

package org.entando.kubernetes.controller.databaseservice;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Service;
import java.util.Locale;
import org.entando.kubernetes.controller.spi.common.DbmsVendorConfig;
import org.entando.kubernetes.controller.spi.container.ProvidedDatabaseCapability;
import org.entando.kubernetes.controller.spi.result.AbstractServiceResult;
import org.entando.kubernetes.controller.spi.result.ServiceDeploymentResult;
import org.entando.kubernetes.model.common.AbstractServerStatus;

//TODO move to org.entando.kubernetes.controller.support
public class DatabaseDeploymentResult extends AbstractServiceResult implements ServiceDeploymentResult<DatabaseDeploymentResult> {

    /*migrate to DbmsVendorConfig*/
    private final DbmsVendorConfig vendor;
    private final String databaseName;
    private final String databaseSecretName;
    private final Pod pod;
    private AbstractServerStatus status;

    public DatabaseDeploymentResult(Service service, DbmsVendorConfig vendor, String databaseName, String databaseSecretName, Pod pod) {
        super(service);
        this.pod = pod;
        this.vendor = vendor;
        this.databaseName = databaseName;
        this.databaseSecretName = databaseSecretName;
    }

    @Override
    public DatabaseDeploymentResult withStatus(AbstractServerStatus status) {
        this.status = status;
        this.status.setAdminSecretName(databaseSecretName);
        this.status.putDerivedDeploymentParameter(ProvidedDatabaseCapability.DATABASE_NAME_PARAMETER, databaseName);
        this.status.putDerivedDeploymentParameter(ProvidedDatabaseCapability.DBMS_VENDOR_PARAMETER, vendor.name().toLowerCase(Locale.ROOT));
        return this;
    }

    public AbstractServerStatus getStatus() {
        return status;
    }

    @Override
    public Pod getPod() {
        return pod;
    }
}
