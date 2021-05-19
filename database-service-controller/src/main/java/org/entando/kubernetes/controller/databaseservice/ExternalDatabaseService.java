package org.entando.kubernetes.controller.databaseservice;

import org.entando.kubernetes.controller.spi.common.DbmsVendorConfig;
import org.entando.kubernetes.controller.spi.deployable.ExternalService;
import org.entando.kubernetes.model.common.DbmsVendor;
import org.entando.kubernetes.model.externaldatabase.EntandoDatabaseService;

public class ExternalDatabaseService implements ExternalService {

    private final EntandoDatabaseService newEntandoDatabaseService;

    ExternalDatabaseService(EntandoDatabaseService newEntandoDatabaseService) {
        this.newEntandoDatabaseService = newEntandoDatabaseService;
    }

    @Override
    public int getPort() {
        return newEntandoDatabaseService.getSpec().getPort()
                .orElse(DbmsVendorConfig.valueOf(newEntandoDatabaseService.getSpec().getDbms().orElse(DbmsVendor.POSTGRESQL).name())
                        .getDefaultPort());
    }

    @Override
    public boolean getCreateDelegateService() {
        return true;
    }

    @Override
    public String getHost() {
        return newEntandoDatabaseService.getSpec().getHost().orElseThrow(IllegalStateException::new);
    }
}
