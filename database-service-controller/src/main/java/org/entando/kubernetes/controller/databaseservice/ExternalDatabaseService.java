package org.entando.kubernetes.controller.databaseservice;

import static org.entando.kubernetes.controller.databaseservice.EntandoDatabaseServiceHelper.strategyFor;

import org.entando.kubernetes.controller.spi.deployable.ExternalService;
import org.entando.kubernetes.model.externaldatabase.EntandoDatabaseService;

public class ExternalDatabaseService implements ExternalService {

    private final EntandoDatabaseService newEntandoDatabaseService;

    ExternalDatabaseService(EntandoDatabaseService newEntandoDatabaseService) {
        this.newEntandoDatabaseService = newEntandoDatabaseService;
    }

    @Override
    public int getPort() {
        return newEntandoDatabaseService.getSpec().getPort().orElse(strategyFor(newEntandoDatabaseService).getPort());
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
