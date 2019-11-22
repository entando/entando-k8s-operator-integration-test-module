package org.entando.kubernetes.cdi;

import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.dsl.internal.CustomResourceOperationsImpl;
import io.fabric8.kubernetes.internal.KubernetesDeserializer;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.entando.kubernetes.controller.k8sclient.DeploymentClient;
import org.entando.kubernetes.controller.k8sclient.EntandoResourceClient;
import org.entando.kubernetes.controller.k8sclient.IngressClient;
import org.entando.kubernetes.controller.k8sclient.PersistentVolumeClaimClient;
import org.entando.kubernetes.controller.k8sclient.PodClient;
import org.entando.kubernetes.controller.k8sclient.SecretClient;
import org.entando.kubernetes.controller.k8sclient.ServiceAccountClient;
import org.entando.kubernetes.controller.k8sclient.ServiceClient;
import org.entando.kubernetes.controller.k8sclient.SimpleK8SClient;
import org.entando.kubernetes.model.DoneableEntandoCustomResource;
import org.entando.kubernetes.model.EntandoCustomResource;
import org.entando.kubernetes.model.app.EntandoApp;
import org.entando.kubernetes.model.app.EntandoAppOperationFactory;
import org.entando.kubernetes.model.externaldatabase.ExternalDatabase;
import org.entando.kubernetes.model.externaldatabase.ExternalDatabaseOperationFactory;
import org.entando.kubernetes.model.infrastructure.EntandoClusterInfrastructure;
import org.entando.kubernetes.model.infrastructure.EntandoClusterInfrastructureOperationFactory;
import org.entando.kubernetes.model.keycloakserver.KeycloakServer;
import org.entando.kubernetes.model.keycloakserver.KeycloakServerOperationFactory;
import org.entando.kubernetes.model.link.EntandoAppPluginLink;
import org.entando.kubernetes.model.link.EntandoAppPluginLinkOperationFactory;
import org.entando.kubernetes.model.plugin.EntandoPlugin;
import org.entando.kubernetes.model.plugin.EntandoPluginOperationFactory;

@ApplicationScoped
public class DefaultSimpleK8SClient implements SimpleK8SClient<EntandoResourceClient> {

    static {
        KubernetesDeserializer.registerCustomKind("entando.org/v1alpha1#EntandoApp", EntandoApp.class);
        KubernetesDeserializer.registerCustomKind("entando.org/v1alpha1#EntandoPlugin", EntandoPlugin.class);
        KubernetesDeserializer.registerCustomKind("entando.org/v1alpha1#EntandoClusterInfrastructure", EntandoClusterInfrastructure.class);
        KubernetesDeserializer.registerCustomKind("entando.org/v1alpha1#ExternalDatabase", ExternalDatabase.class);
        KubernetesDeserializer.registerCustomKind("entando.org/v1alpha1#EntandoKeycloakServer", KeycloakServer.class);
        KubernetesDeserializer.registerCustomKind("entando.org/v1alpha1#EntandoAppPluginLink", EntandoAppPluginLink.class);
    }

    private final DefaultKubernetesClient kubernetesClient;
    private ServiceClient serviceClient;
    private PodClient podClient;
    private SecretClient secretClient;
    private EntandoResourceClient entandoResourceClient;
    private DeploymentClient deploymentClient;
    private IngressClient ingressClient;
    private PersistentVolumeClaimClient persistentVolumeClaimClient;
    private ServiceAccountClient serviceAccountClient;

    @Inject
    public DefaultSimpleK8SClient(DefaultKubernetesClient kubernetesClient) {
        this.kubernetesClient = kubernetesClient;
    }

    @Override
    public ServiceClient services() {
        if (this.serviceClient == null) {
            this.serviceClient = new DefaultServiceClient(kubernetesClient);
        }
        return this.serviceClient;
    }

    @Override
    public PodClient pods() {
        if (this.podClient == null) {
            this.podClient = new DefaultPodClient(kubernetesClient);
        }
        return this.podClient;
    }

    @Override
    public SecretClient secrets() {
        if (this.secretClient == null) {
            this.secretClient = new DefaultSecretClient(kubernetesClient);
        }
        return this.secretClient;
    }

    @Override
    public EntandoResourceClient entandoResources() {
        if (this.entandoResourceClient == null) {

            List<CustomResourceOperationsImpl<?
                    extends EntandoCustomResource, ?
                    extends KubernetesResourceList, ?
                    extends DoneableEntandoCustomResource>> operations = new ArrayList<>();
            operations.add(ExternalDatabaseOperationFactory.produceAllExternalDatabases(kubernetesClient));
            operations.add(KeycloakServerOperationFactory.produceAllKeycloakServers(kubernetesClient));
            operations.add(EntandoClusterInfrastructureOperationFactory.produceAllEntandoClusterInfrastructures(kubernetesClient));
            operations.add(EntandoAppOperationFactory.produceAllEntandoApps(kubernetesClient));
            operations.add(EntandoPluginOperationFactory.produceAllEntandoPlugins(kubernetesClient));
            operations.add(EntandoAppPluginLinkOperationFactory.produceAllEntandoAppPluginLinks(kubernetesClient));
            this.entandoResourceClient = new DefaultEntandoResourceClient(kubernetesClient, operations);
        }
        return this.entandoResourceClient;
    }

    @Override
    public ServiceAccountClient serviceAccounts() {
        if (this.serviceAccountClient == null) {
            this.serviceAccountClient = new DefaultServiceAccountClient(kubernetesClient);
        }
        return this.serviceAccountClient;
    }

    @Override
    public DeploymentClient deployments() {
        if (this.deploymentClient == null) {
            this.deploymentClient = new DefaultDeploymentClient(kubernetesClient);
        }
        return this.deploymentClient;
    }

    @Override
    public IngressClient ingresses() {
        if (this.ingressClient == null) {
            this.ingressClient = new DefaultIngressClient(kubernetesClient);
        }
        return this.ingressClient;
    }

    @Override
    public PersistentVolumeClaimClient persistentVolumeClaims() {
        if (this.persistentVolumeClaimClient == null) {
            this.persistentVolumeClaimClient = new DefaultPersistentVolumeClaimClient(kubernetesClient);
        }
        return this.persistentVolumeClaimClient;
    }

}
