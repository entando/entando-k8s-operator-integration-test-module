package org.entando.kubernetes.controller.link;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.entando.kubernetes.controller.KubeUtils;
import org.entando.kubernetes.controller.ServiceDeploymentResult;
import org.entando.kubernetes.controller.spi.Ingressing;
import org.entando.kubernetes.controller.spi.IngressingPathOnPort;
import org.entando.kubernetes.model.app.EntandoApp;
import org.entando.kubernetes.model.plugin.EntandoPlugin;

public class EntandoLinkedPluginIngressing implements Ingressing<IngressingPathOnPort> {

    private final EntandoApp entandoApp;
    private final List<IngressingPathOnPort> ingressingPaths;
    private final ServiceDeploymentResult entandoAppDeploymentResult;
    private final ServiceDeploymentResult entandoPluginDeploymentResult;

    public EntandoLinkedPluginIngressing(EntandoApp entandoApp, EntandoPlugin entandoPlugin,
            ServiceDeploymentResult entandoAppDeploymentResult,
            ServiceDeploymentResult entandoPluginDeploymentResult) {
        this.entandoApp = entandoApp;
        this.ingressingPaths = Arrays.asList(new IngressingPathOnPort() {
            @Override
            public int getPort() {
                return 8081;
            }

            @Override
            public String getWebContextPath() {
                return entandoPlugin.getSpec().getIngressPath();
            }

            @Override
            public Optional<String> getHealthCheckPath() {
                return Optional.of(getWebContextPath() + entandoPlugin.getSpec().getHealthCheckPath());
            }
        });
        this.entandoAppDeploymentResult = entandoAppDeploymentResult;
        this.entandoPluginDeploymentResult = entandoPluginDeploymentResult;
    }

    @Override
    public List<IngressingPathOnPort> getIngressingContainers() {
        return ingressingPaths;
    }

    @Override
    public String getIngressName() {
        return entandoApp.getMetadata().getName() + "-" + KubeUtils.DEFAULT_INGRESS_SUFFIX;
    }

    @Override
    public String getIngressNamespace() {
        return entandoApp.getMetadata().getNamespace();
    }

    @Override
    public String getNameQualifier() {
        return KubeUtils.DEFAULT_SERVER_QUALIFIER; //Same as EntandoApp 'server'
    }

    public EntandoApp getEntandoApp() {
        return entandoApp;
    }

    public ServiceDeploymentResult getEntandoAppDeploymentResult() {
        return entandoAppDeploymentResult;
    }

    public ServiceDeploymentResult getEntandoPluginDeploymentResult() {
        return entandoPluginDeploymentResult;
    }
}
