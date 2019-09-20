package org.entando.kubernetes.model.keycloakserver;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.fabric8.kubernetes.client.CustomResourceList;

@JsonDeserialize
public class KeycloakServerList extends CustomResourceList<KeycloakServer> {

}
