package org.entando.kubernetes.model.app;

import static java.lang.Thread.sleep;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.internal.CustomResourceOperationsImpl;
import java.util.List;

public final class EntandoAppOperationFactory {

    private static final int NOT_FOUND = 404;
    private static CustomResourceDefinition entandoAppCrd;

    private EntandoAppOperationFactory() {
    }

    public static CustomResourceOperationsImpl<EntandoApp, EntandoAppList,
            DoneableEntandoApp> produceAllEntandoApps(
            KubernetesClient client) throws InterruptedException {
        synchronized (EntandoAppOperationFactory.class) {
            entandoAppCrd = client.customResourceDefinitions().withName(EntandoApp.CRD_NAME).get();
            if (entandoAppCrd == null) {
                List<HasMetadata> list = client.load(Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("crd/EntandoAppCRD.yaml")).get();
                entandoAppCrd = (CustomResourceDefinition) list.get(0);
                // see issue https://github.com/fabric8io/kubernetes-client/issues/1486
                entandoAppCrd.getSpec().getValidation().getOpenAPIV3Schema().setDependencies(null);
                client.customResourceDefinitions().create(entandoAppCrd);
            }

        }
        CustomResourceOperationsImpl<EntandoApp, EntandoAppList,
                DoneableEntandoApp>
                oper = (CustomResourceOperationsImpl<EntandoApp, EntandoAppList,
                DoneableEntandoApp>) client
                .customResources(entandoAppCrd, EntandoApp.class, EntandoAppList.class,
                        DoneableEntandoApp.class);
        while (notAvailable(oper)) {
            sleep(100);
        }
        return oper;
    }

    private static boolean notAvailable(
            CustomResourceOperationsImpl<EntandoApp, EntandoAppList,
                    DoneableEntandoApp> oper) {
        try {
            oper.inNamespace("default").list().getItems().size();
            return false;
        } catch (KubernetesClientException e) {
            return e.getCode() == NOT_FOUND;
        }
    }

}
