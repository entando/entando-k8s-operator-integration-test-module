package org.entando.kubernetes.controller.databaseservice;

import static java.lang.String.format;
import static org.entando.kubernetes.controller.databaseservice.EntandoDatabaseServiceHelper.strategyFor;

import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.extensions.Ingress;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.entando.kubernetes.controller.spi.common.DbmsDockerVendorStrategy;
import org.entando.kubernetes.controller.spi.common.NameUtils;
import org.entando.kubernetes.controller.spi.common.SecretUtils;
import org.entando.kubernetes.controller.spi.container.DeployableContainer;
import org.entando.kubernetes.controller.spi.deployable.Deployable;
import org.entando.kubernetes.controller.spi.deployable.ExternalService;
import org.entando.kubernetes.controller.spi.deployable.Secretive;
import org.entando.kubernetes.model.common.EntandoCustomResource;
import org.entando.kubernetes.model.externaldatabase.EntandoDatabaseService;

public class DatabaseServiceDeployable implements Deployable<DatabaseDeploymentResult>, Secretive {

    private final EntandoDatabaseService newEntandoDatabaseService;
    private final DbmsDockerVendorStrategy dbmsVendor;
    private final EntandoCustomResource customResource;
    protected List<DeployableContainer> containers;

    public DatabaseServiceDeployable(EntandoDatabaseService newEntandoDatabaseService) {
        this.dbmsVendor = strategyFor(newEntandoDatabaseService);
        this.customResource = newEntandoDatabaseService;
        this.containers = Collections
                .singletonList(new DatabaseServiceContainer(newEntandoDatabaseService,
                        buildVariableInitializer(strategyFor(newEntandoDatabaseService)),
                        strategyFor(newEntandoDatabaseService),
                        getPortOverride(newEntandoDatabaseService)));
        this.newEntandoDatabaseService = newEntandoDatabaseService;
    }

    @Override
    public Optional<ExternalService> getExternalService() {
        if (newEntandoDatabaseService.getSpec().getCreateDeployment().orElse(false)) {
            return Optional.empty();
        } else {
            return Optional.of(new ExternalDatabaseService(newEntandoDatabaseService));
        }
    }

    private static Integer getPortOverride(EntandoDatabaseService newEntandoDatabaseService) {
        return newEntandoDatabaseService.getSpec().getPort().orElse(null);
    }

    protected String getDatabaseAdminSecretName() {
        return newEntandoDatabaseService.getSpec().getSecretName().orElse(NameUtils.standardAdminSecretName(newEntandoDatabaseService));
    }

    @Override
    public List<Secret> getSecrets() {
        if (newEntandoDatabaseService.getSpec().getSecretName().isPresent()) {
            //because it already exists
            return Collections.emptyList();
        } else {
            Secret secret = SecretUtils.generateSecret(
                    customResource,
                    getDatabaseAdminSecretName(),
                    dbmsVendor.getDefaultAdminUsername()
            );
            return Collections.singletonList(secret);
        }
    }

    protected String getDatabaseName() {
        return newEntandoDatabaseService.getSpec().getDatabaseName().orElse(
                NameUtils.databaseCompliantName(customResource, NameUtils.DB_NAME_QUALIFIER, dbmsVendor.getVendorConfig()));
    }

    @Override
    public String getServiceAccountToUse() {
        return getDefaultServiceAccountName();
    }

    protected DatabaseVariableInitializer buildVariableInitializer(DbmsDockerVendorStrategy vendorStrategy) {
        switch (vendorStrategy) {
            case CENTOS_MYSQL:
            case RHEL_MYSQL:
                return vars ->
                        //No DB creation. Dbs are created during schema creation
                        vars.add(new EnvVar("MYSQL_ROOT_PASSWORD", null,
                                SecretUtils.secretKeyRef(getDatabaseAdminSecretName(), SecretUtils.PASSSWORD_KEY)));
            case CENTOS_POSTGRESQL:
            case RHEL_POSTGRESQL:
                return vars -> {
                    vars.add(new EnvVar("POSTGRESQL_DATABASE", getDatabaseName(), null));
                    // This username will not be used, as we will be creating schema/user pairs,
                    // but without it the DB isn't created.
                    vars.add(new EnvVar("POSTGRESQL_USER", getDatabaseName() + "_user", null));
                    vars.add(new EnvVar("POSTGRESQL_PASSWORD", null,
                            SecretUtils.secretKeyRef(getDatabaseAdminSecretName(), SecretUtils.PASSSWORD_KEY)));
                    vars.add(new EnvVar("POSTGRESQL_ADMIN_PASSWORD", null,
                            SecretUtils.secretKeyRef(getDatabaseAdminSecretName(), SecretUtils.PASSSWORD_KEY)));

                };
            default:
                throw new IllegalStateException(
                        format("The DBMS %s not supported for containerized deployments", vendorStrategy.getName()));
        }
    }

    @Override
    public Optional<Long> getFileSystemUserAndGroupId() {
        return dbmsVendor.getFileSystemUserGroupid();
    }

    @Override
    public List<DeployableContainer> getContainers() {
        return containers;
    }

    @Override
    public Optional<String> getQualifier() {
        return Optional.empty();
    }

    @Override
    public EntandoCustomResource getCustomResource() {
        return customResource;
    }

    @Override
    public DatabaseDeploymentResult createResult(Deployment deployment, Service service, Ingress ingress, Pod pod) {
        return new DatabaseDeploymentResult(service, dbmsVendor.getVendorConfig(), getDatabaseName(), getDatabaseAdminSecretName(), pod);
    }
}
