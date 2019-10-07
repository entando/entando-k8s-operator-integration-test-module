package org.entando.kubernetes.model;

import java.util.Optional;

public interface HasIngress {

    Optional<String> getIngressHostName();

    Optional<String> getTlsSecretName();
}
