package org.entando.kubernetes.model.link;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@JsonSerialize
@JsonDeserialize
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
public class EntandoAppPluginLinkSpec implements KubernetesResource {

    private String entandoAppNamespace;
    private String entandoAppName;
    private String entandoPluginNamespace;
    private String entandoPluginName;

    public EntandoAppPluginLinkSpec() {
        //Required for JSON deserialization
    }

    public EntandoAppPluginLinkSpec(String entandoAppNamespace, String entandoAppName, String entandoPluginNamespace,
            String entandoPluginName) {
        this.entandoAppNamespace = entandoAppNamespace;
        this.entandoAppName = entandoAppName;
        this.entandoPluginNamespace = entandoPluginNamespace;
        this.entandoPluginName = entandoPluginName;
    }

    public String getEntandoAppName() {
        return entandoAppName;
    }

    public String getEntandoAppNamespace() {
        return entandoAppNamespace;
    }

    public String getEntandoPluginName() {
        return entandoPluginName;
    }

    public String getEntandoPluginNamespace() {
        return entandoPluginNamespace;
    }

}