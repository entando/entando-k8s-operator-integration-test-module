package org.entando.kubernetes.model.common;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomResourceReference extends ResourceReference {

    private String apiVersion;
    private String kind;

    @JsonCreator
    public CustomResourceReference(@JsonProperty("apiVersion") String apiVersion,
            @JsonProperty("kind") String kind,
            @JsonProperty("namespace") String namespace,
            @JsonProperty("name") String name) {
        super(namespace, name);
        this.apiVersion = apiVersion;
        this.kind = kind;
    }

    public CustomResourceReference() {
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public String getKind() {
        return kind;
    }
}
