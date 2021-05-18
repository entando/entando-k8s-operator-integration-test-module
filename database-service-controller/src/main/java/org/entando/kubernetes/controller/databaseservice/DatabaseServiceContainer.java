package org.entando.kubernetes.controller.databaseservice;

import static java.util.Optional.ofNullable;

import io.fabric8.kubernetes.api.model.EnvVar;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.entando.kubernetes.controller.spi.common.DbmsDockerVendorStrategy;
import org.entando.kubernetes.controller.spi.common.EntandoOperatorSpiConfig;
import org.entando.kubernetes.controller.spi.common.NameUtils;
import org.entando.kubernetes.controller.spi.container.ConfigurableResourceContainer;
import org.entando.kubernetes.controller.spi.container.DockerImageInfo;
import org.entando.kubernetes.controller.spi.container.HasHealthCommand;
import org.entando.kubernetes.controller.spi.container.PersistentVolumeAware;
import org.entando.kubernetes.controller.spi.container.ServiceBackingContainer;
import org.entando.kubernetes.model.common.EntandoResourceRequirements;
import org.entando.kubernetes.model.externaldatabase.EntandoDatabaseService;

public class DatabaseServiceContainer implements ConfigurableResourceContainer, ServiceBackingContainer,
        PersistentVolumeAware, HasHealthCommand {

    private final EntandoDatabaseService entandoDatabaseService;
    private final DbmsDockerVendorStrategy dbmsVendorDockerStrategy;
    private final DatabaseVariableInitializer variableInitializer;
    private final Integer portOverride;

    public DatabaseServiceContainer(EntandoDatabaseService entandoDatabaseService, DatabaseVariableInitializer variableInitializer,
            DbmsDockerVendorStrategy dbmsVendor, Integer portOverride) {
        this.variableInitializer = variableInitializer;
        this.dbmsVendorDockerStrategy = dbmsVendor;
        this.portOverride = portOverride;
        this.entandoDatabaseService = entandoDatabaseService;
    }

    @Override
    public Optional<EntandoResourceRequirements> getResourceRequirementsOverride() {
        return entandoDatabaseService.getSpec().getResourceRequirements();
    }

    @Override
    public Optional<String> getAccessMode() {
        return Optional.of("ReadWriteOnce");
    }

    @Override
    public Optional<String> getStorageClass() {
        return Optional.ofNullable(this.entandoDatabaseService.getSpec().getStorageClass().orElse(
                EntandoOperatorSpiConfig.getDefaultNonClusteredStorageClass().orElse(null)));
    }

    @Override
    public Optional<Integer> getMaximumStartupTimeSeconds() {
        return Optional.of(90);
    }

    @Override
    public DockerImageInfo getDockerImageInfo() {
        return new DockerImageInfo(dbmsVendorDockerStrategy);
    }

    @Override
    public String getNameQualifier() {
        return NameUtils.DB_NAME_QUALIFIER;
    }

    @Override
    public int getPrimaryPort() {
        return ofNullable(portOverride).orElse(dbmsVendorDockerStrategy.getPort());
    }

    @Override
    public int getMemoryLimitMebibytes() {
        return dbmsVendorDockerStrategy.getDefaultMemoryLimitMebibytes();
    }

    @Override
    public String getVolumeMountPath() {
        return dbmsVendorDockerStrategy.getVolumeMountPath();
    }

    @Override
    public String getHealthCheckCommand() {
        return dbmsVendorDockerStrategy.getHealthCheck();
    }

    @Override
    public List<EnvVar> getEnvironmentVariables() {
        List<EnvVar> vars = new ArrayList<>();
        variableInitializer.addEnvironmentVariables(vars);
        return vars;
    }
}

