package org.entando.kubernetes.model.externaldatabase;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.fabric8.kubernetes.client.CustomResourceList;

@JsonDeserialize
public class ExternalDatabaseList extends CustomResourceList<ExternalDatabase> {

}
