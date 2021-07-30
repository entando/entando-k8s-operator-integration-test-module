package org.entando.kubernetes.controller.databaseservice;

import org.entando.kubernetes.controller.spi.common.DbmsDockerVendorStrategy;
import org.entando.kubernetes.controller.spi.common.EntandoOperatorSpiConfig;
import org.entando.kubernetes.model.capability.CapabilityProvisioningStrategy;
import org.entando.kubernetes.model.common.DbmsVendor;
import org.entando.kubernetes.model.externaldatabase.EntandoDatabaseService;

public class EntandoDatabaseServiceHelper {

    private EntandoDatabaseServiceHelper() {
    }

    public static DbmsDockerVendorStrategy strategyFor(EntandoDatabaseService service) {
        return DbmsDockerVendorStrategy.forVendor(service.getSpec().getDbms().orElse(DbmsVendor.POSTGRESQL),
                EntandoOperatorSpiConfig.getComplianceMode());
    }

    public static Boolean deployDirectly(EntandoDatabaseService entandoDatabaseService) {
        return entandoDatabaseService.getSpec().getProvisioningStrategy().map(CapabilityProvisioningStrategy.DEPLOY_DIRECTLY::equals)
                .or(entandoDatabaseService.getSpec()::getCreateDeployment)
                .orElse(false);
    }
}
