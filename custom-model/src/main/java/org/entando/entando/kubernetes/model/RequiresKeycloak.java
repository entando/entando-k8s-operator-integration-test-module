package org.entando.entando.kubernetes.model;

public interface RequiresKeycloak extends EntandoCustomResource {

    String getKeycloakServerNamespace();

    String getKeycloakServerName();

}
