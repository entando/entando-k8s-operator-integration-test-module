package org.entando.kubernetes.model.plugin;

import static java.lang.Thread.sleep;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.internal.CustomResourceOperationsImpl;
import java.util.List;

public final class EntandoPluginOperationFactory {

    private static final int NOT_FOUND = 404;
    private static CustomResourceDefinition entandoPluginCrd;

    private EntandoPluginOperationFactory() {
    }

    public static CustomResourceOperationsImpl<EntandoPlugin, EntandoPluginList,
            DoneableEntandoPlugin> produceAllEntandoPlugins(
            KubernetesClient client) throws InterruptedException {
        synchronized (EntandoPluginOperationFactory.class) {
            entandoPluginCrd = client.customResourceDefinitions().withName(EntandoPlugin.CRD_NAME).get();
            if (entandoPluginCrd == null) {
                List<HasMetadata> list = client.load(Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("crd/EntandoPluginCRD.yaml")).get();
                entandoPluginCrd = (CustomResourceDefinition) list.get(0);
                // see issue https://github.com/fabric8io/kubernetes-client/issues/1486
                entandoPluginCrd.getSpec().getValidation().getOpenAPIV3Schema().setDependencies(null);
                client.customResourceDefinitions().create(entandoPluginCrd);
            }

        }
        CustomResourceOperationsImpl<EntandoPlugin, EntandoPluginList,
                DoneableEntandoPlugin>
                oper = (CustomResourceOperationsImpl<EntandoPlugin, EntandoPluginList,
                DoneableEntandoPlugin>) client
                .customResources(entandoPluginCrd, EntandoPlugin.class, EntandoPluginList.class,
                        DoneableEntandoPlugin.class);
        while (notAvailable(oper)) {
            sleep(100);
        }
        return oper;
    }

    private static boolean notAvailable(
            CustomResourceOperationsImpl<EntandoPlugin, EntandoPluginList,
                    DoneableEntandoPlugin> oper) {
        try {
            oper.inNamespace("default").list().getItems().size();
            return false;
        } catch (KubernetesClientException e) {
            return e.getCode() == NOT_FOUND;
        }
    }

}
