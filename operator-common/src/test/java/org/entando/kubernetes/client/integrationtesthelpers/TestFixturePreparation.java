/*
 *
 * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 *  This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 */

package org.entando.kubernetes.client.integrationtesthelpers;

import io.fabric8.kubernetes.client.AutoAdaptableKubernetesClient;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.utils.HttpClientUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import org.entando.kubernetes.client.EntandoOperatorTestConfig;
import org.entando.kubernetes.controller.support.common.EntandoOperatorConfigProperty;
import org.entando.kubernetes.controller.support.common.KubeUtils;
import org.entando.kubernetes.controller.support.creators.IngressCreator;
import org.entando.kubernetes.model.EntandoBaseCustomResource;
import org.entando.kubernetes.test.common.CertificateSecretHelper;

public final class TestFixturePreparation {

    public static final String ENTANDO_CONTROLLERS_NAMESPACE = EntandoOperatorTestConfig.calculateNameSpace("entando-controllers");
    public static final String CURRENT_ENTANDO_RESOURCE_VERSION = "v1";

    private TestFixturePreparation() {

    }

    public static AutoAdaptableKubernetesClient newClient() {
        try {
            AutoAdaptableKubernetesClient result = buildKubernetesClient();
            initializeTls(result);
            return result;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void initializeTls(AutoAdaptableKubernetesClient result) throws IOException {
        String domainSuffix = IngressCreator.determineRoutingSuffix(result.getMasterUrl().getHost());
        Path certRoot = Paths.get(EntandoOperatorTestConfig.getTestsCertRoot());
        Path tlsPath = certRoot.resolve(domainSuffix);
        CertificateSecretHelper.buildCertificateSecretsFromDirectory(result.getNamespace(), tlsPath)
                .forEach(result.secrets()::createOrReplace);
        System.setProperty(EntandoOperatorConfigProperty.ENTANDO_DISABLE_KEYCLOAK_SSL_REQUIREMENT.getJvmSystemProperty(),
                String.valueOf(HttpTestHelper.getDefaultProtocol().equals("http")));
    }

    private static AutoAdaptableKubernetesClient buildKubernetesClient() {
        ConfigBuilder configBuilder = new ConfigBuilder().withTrustCerts(true).withConnectionTimeout(30000).withRequestTimeout(30000);
        EntandoOperatorTestConfig.getKubernetesMasterUrl().ifPresent(configBuilder::withMasterUrl);
        EntandoOperatorTestConfig.getKubernetesUsername().ifPresent(configBuilder::withUsername);
        EntandoOperatorTestConfig.getKubernetesPassword().ifPresent(configBuilder::withPassword);
        Config config = configBuilder.build();
        OkHttpClient httpClient = HttpClientUtils.createHttpClient(config);
        AutoAdaptableKubernetesClient result = new AutoAdaptableKubernetesClient(httpClient, config);
        if (result.namespaces().withName(ENTANDO_CONTROLLERS_NAMESPACE).get() == null) {
            createNamespace(result, ENTANDO_CONTROLLERS_NAMESPACE);
        }
        //Has to be in entando-controllers
        if (!ENTANDO_CONTROLLERS_NAMESPACE.equals(result.getNamespace())) {
            result.close();
            config.setNamespace(ENTANDO_CONTROLLERS_NAMESPACE);
            result = new AutoAdaptableKubernetesClient(HttpClientUtils.createHttpClient(config), config);
        }
        ensureRedHatRegistryCredentials(result);
        return result;
    }

    private static void ensureRedHatRegistryCredentials(AutoAdaptableKubernetesClient result) {
        if (result.secrets().inNamespace(ENTANDO_CONTROLLERS_NAMESPACE).withName("redhat-registry").get() == null) {
            EntandoOperatorTestConfig.getRedhatRegistryCredentials().ifPresent(s ->
                    result.secrets().inNamespace(ENTANDO_CONTROLLERS_NAMESPACE).createNew().withNewMetadata()
                            .withNamespace(ENTANDO_CONTROLLERS_NAMESPACE)
                            .withName("redhat-registry")
                            .endMetadata()
                            .addToStringData(".dockerconfigjson", s)
                            .withType("kubernetes.io/dockerconfigjson")
                            .done());
        }
    }

    public static void prepareTestFixture(KubernetesClient client, TestFixtureRequest testFixtureRequest) {
        for (Entry<String, List<Class<? extends EntandoBaseCustomResource<?>>>> entry :
                testFixtureRequest.getRequiredDeletions().entrySet()) {
            if (client.namespaces().withName(entry.getKey()).get() != null) {
                for (Class<? extends EntandoBaseCustomResource<?>> type : entry.getValue()) {
                    //This is a bit heavy-handed, but we need  to make absolutely sure the pods are deleted before the test starts
                    //Pods are considered 'deleted' even if they are still gracefully shutting down and the second or two
                    // it takes to shut down can interfere with subsequent pod watchers.
                    DeletionWaiter.delete(client.apps().deployments()).fromNamespace(entry.getKey())
                            .withLabel(KubeUtils.getKindOf(type))
                            .waitingAtMost(60, TimeUnit.SECONDS);
                    DeletionWaiter.delete(client.pods()).fromNamespace(entry.getKey())
                            .withLabel(KubeUtils.getKindOf(type))
                            .waitingAtMost(60, TimeUnit.SECONDS);
                    new CustomResourceDeletionWaiter(client, KubeUtils.getKindOf(type)).fromNamespace(entry.getKey())
                            .waitingAtMost(120, TimeUnit.SECONDS);
                    DeletionWaiter.delete(client.persistentVolumeClaims()).fromNamespace(entry.getKey())
                            .withLabel(KubeUtils.getKindOf(type))
                            .waitingAtMost(60, TimeUnit.SECONDS);
                }
            } else {
                createNamespace(client, entry.getKey());
            }
        }
    }

    public static void createNamespace(KubernetesClient client, String namespace) {
        client.namespaces().createNew().withNewMetadata().withName(namespace)
                .addToLabels("testType", "end-to-end")
                .endMetadata().done();
    }
}