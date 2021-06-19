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

package org.entando.kubernetes.controller.app;

import org.entando.kubernetes.controller.spi.common.EntandoOperatorComplianceMode;
import org.entando.kubernetes.controller.spi.common.EntandoOperatorSpiConfig;
import org.entando.kubernetes.controller.spi.container.KeycloakName;
import org.entando.kubernetes.controller.spi.deployable.SsoConnectionInfo;
import org.entando.kubernetes.model.app.EntandoApp;
import org.entando.kubernetes.model.common.DbmsVendor;
import org.entando.kubernetes.model.common.KeycloakToUse;

public class EntandoAppHelper {

    private EntandoAppHelper() {

    }

    public static DbmsVendor determineDbmsVendor(EntandoApp entandoApp) {
        if (EntandoOperatorSpiConfig.getComplianceMode() == EntandoOperatorComplianceMode.REDHAT) {
            return entandoApp.getSpec().getDbms().orElse(DbmsVendor.POSTGRESQL);
        } else {
            return entandoApp.getSpec().getDbms().orElse(DbmsVendor.EMBEDDED);
        }
    }

    public static String determineRealm(EntandoApp entandoApp, SsoConnectionInfo ssoConnectionInfo) {
        return entandoApp.getSpec().getKeycloakToUse().flatMap(KeycloakToUse::getRealm).or(() -> ssoConnectionInfo.getDefaultRealm())
                .orElse(KeycloakName.ENTANDO_DEFAULT_KEYCLOAK_REALM);
    }
}
