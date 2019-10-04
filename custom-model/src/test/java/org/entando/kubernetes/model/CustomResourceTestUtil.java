package org.entando.kubernetes.model;

import io.fabric8.kubernetes.client.CustomResourceList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.internal.CustomResourceOperationsImpl;

public interface CustomResourceTestUtil {

    default void prepareNamespace(CustomResourceOperationsImpl oper, String namespace) throws InterruptedException {
        if (getClient().namespaces().withName(namespace).get() == null) {
            getClient().namespaces().createNew().withNewMetadata().withName(namespace).endMetadata().done();
        } else {
            while (((CustomResourceList) oper.inNamespace(namespace).list()).getItems().size() > 0) {
                try {
                    oper.inNamespace(namespace)
                            .delete(((CustomResourceList) oper.inNamespace(namespace).list()).getItems().get(0));
                    Thread.sleep(100);
                } catch (IndexOutOfBoundsException e) {
                    return;
                }
            }
        }
    }

    KubernetesClient getClient();
}
