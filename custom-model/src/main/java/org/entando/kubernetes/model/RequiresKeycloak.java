package org.entando.kubernetes.model;

import java.util.Optional;

public interface RequiresKeycloak {

    Optional<String> getKeycloakSecretToUse();

}
