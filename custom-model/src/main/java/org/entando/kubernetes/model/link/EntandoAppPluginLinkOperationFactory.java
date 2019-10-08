package org.entando.kubernetes.model.link;

import static java.lang.Thread.sleep;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.internal.CustomResourceOperationsImpl;
import java.util.List;

public final class EntandoAppPluginLinkOperationFactory {

    private static final int NOT_FOUND = 404;
    private static CustomResourceDefinition entandoAppPluginLinkCrd;

    private EntandoAppPluginLinkOperationFactory() {
    }

    public static CustomResourceOperationsImpl<EntandoAppPluginLink, EntandoAppPluginLinkList,
            DoneableEntandoAppPluginLink> produceAllEntandoAppPluginLinks(
            KubernetesClient client) throws InterruptedException {
        synchronized (EntandoAppPluginLinkOperationFactory.class) {
            entandoAppPluginLinkCrd = client.customResourceDefinitions().withName(EntandoAppPluginLink.CRD_NAME).get();
            if (entandoAppPluginLinkCrd == null) {
                List<HasMetadata> list = client.load(Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("crd/EntandoAppPluginLinkCRD.yaml")).get();
                entandoAppPluginLinkCrd = (CustomResourceDefinition) list.get(0);
                // see issue https://github.com/fabric8io/kubernetes-client/issues/1486
                entandoAppPluginLinkCrd.getSpec().getValidation().getOpenAPIV3Schema().setDependencies(null);
                client.customResourceDefinitions().create(entandoAppPluginLinkCrd);
            }

        }
        CustomResourceOperationsImpl<EntandoAppPluginLink, EntandoAppPluginLinkList,
                DoneableEntandoAppPluginLink>
                oper = (CustomResourceOperationsImpl<EntandoAppPluginLink, EntandoAppPluginLinkList,
                DoneableEntandoAppPluginLink>) client
                .customResources(entandoAppPluginLinkCrd, EntandoAppPluginLink.class, EntandoAppPluginLinkList.class,
                        DoneableEntandoAppPluginLink.class);
        while (notAvailable(oper)) {
            sleep(100);
        }
        return oper;
    }

    private static boolean notAvailable(
            CustomResourceOperationsImpl<EntandoAppPluginLink, EntandoAppPluginLinkList,
                    DoneableEntandoAppPluginLink> oper) {
        try {
            oper.inNamespace("default").list().getItems().size();
            return false;
        } catch (KubernetesClientException e) {
            return e.getCode() == NOT_FOUND;
        }
    }

}
