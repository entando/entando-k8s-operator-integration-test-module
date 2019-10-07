package org.entando.kubernetes.model.infrastructure;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.fabric8.kubernetes.client.CustomResourceList;

@JsonDeserialize
public class EntandoClusterInfrastructureList extends CustomResourceList<EntandoClusterInfrastructure> {

}
