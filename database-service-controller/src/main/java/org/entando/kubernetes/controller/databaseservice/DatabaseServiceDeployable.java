package org.entando.kubernetes.controller.databaseservice;

import io.fabric8.kubernetes.api.model.Secret;
import java.util.Collections;
import java.util.List;
import org.entando.kubernetes.controller.spi.common.DbmsDockerVendorStrategy;
import org.entando.kubernetes.controller.spi.common.EntandoOperatorSpiConfig;
import org.entando.kubernetes.controller.spi.database.DatabaseDeployable;
import org.entando.kubernetes.model.externaldatabase.EntandoDatabaseService;

public class DatabaseServiceDeployable extends DatabaseDeployable {

    private final EntandoDatabaseService newEntandoDatabaseService;

    public DatabaseServiceDeployable(EntandoDatabaseService newEntandoDatabaseService) {
        super(getVendorStrategy(newEntandoDatabaseService), newEntandoDatabaseService, getPortOverride(newEntandoDatabaseService));
        this.newEntandoDatabaseService = newEntandoDatabaseService;
        super.containers = Collections.singletonList(new DatabaseServiceContainer(newEntandoDatabaseService,
                buildVariableInitializer(getVendorStrategy(newEntandoDatabaseService)),
                getVendorStrategy(newEntandoDatabaseService),
                getPortOverride(newEntandoDatabaseService)));

    }

    private static Integer getPortOverride(EntandoDatabaseService newEntandoDatabaseService) {
        return newEntandoDatabaseService.getSpec().getPort().orElse(null);
    }

    private static DbmsDockerVendorStrategy getVendorStrategy(EntandoDatabaseService newEntandoDatabaseService) {
        return DbmsDockerVendorStrategy
                .forVendor(newEntandoDatabaseService.getSpec().getDbms(), EntandoOperatorSpiConfig.getComplianceMode());
    }

    @Override
    protected String getDatabaseAdminSecretName() {
        return newEntandoDatabaseService.getSpec().getSecretName().orElse(super.getDatabaseAdminSecretName());
    }

    @Override
    public List<Secret> getSecrets() {
        if (newEntandoDatabaseService.getSpec().getSecretName().isPresent()) {
            //because it already exists
            return Collections.emptyList();
        } else {
            return super.getSecrets();
        }
    }

    @Override
    protected String getDatabaseName() {
        return newEntandoDatabaseService.getSpec().getDatabaseName().orElse(super.getDatabaseName());
    }
}
