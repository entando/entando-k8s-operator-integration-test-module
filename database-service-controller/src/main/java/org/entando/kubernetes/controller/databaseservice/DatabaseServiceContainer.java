package org.entando.kubernetes.controller.databaseservice;

import java.util.Optional;
import org.entando.kubernetes.controller.spi.common.DbmsDockerVendorStrategy;
import org.entando.kubernetes.controller.spi.container.ConfigurableResourceContainer;
import org.entando.kubernetes.controller.spi.database.DatabaseContainer;
import org.entando.kubernetes.controller.spi.database.DatabaseVariableInitializer;
import org.entando.kubernetes.model.EntandoResourceRequirements;
import org.entando.kubernetes.model.externaldatabase.EntandoDatabaseService;

public class DatabaseServiceContainer extends DatabaseContainer implements ConfigurableResourceContainer {

    private final EntandoDatabaseService entandoDatabaseService;

    public DatabaseServiceContainer(EntandoDatabaseService entandoDatabaseService, DatabaseVariableInitializer variableInitializer,
            DbmsDockerVendorStrategy dbmsVendor, Integer portOverride) {
        super(variableInitializer, dbmsVendor, portOverride);
        this.entandoDatabaseService = entandoDatabaseService;
    }

    @Override
    public Optional<EntandoResourceRequirements> getResourceRequirementsOverride() {
        return entandoDatabaseService.getSpec().getResourceRequirements();
    }
}
