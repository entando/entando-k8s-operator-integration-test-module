package org.entando.entando.kubernetes.model.app;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.fabric8.kubernetes.client.CustomResourceList;

@JsonDeserialize
public class EntandoAppList extends CustomResourceList<EntandoApp> {

}
