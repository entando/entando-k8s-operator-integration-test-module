package org.entando.kubernetes.model.plugin;

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
public class ExpectedRole implements KubernetesResource {

    private String code;
    private String name;

    public ExpectedRole() {
        super();
    }

    public ExpectedRole(final String code) {
        this();
        this.setCode(code);
    }

    public ExpectedRole(String code, String name) {
        this(code);
        setName(name);
    }

    public String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public final void setCode(String code) {
        this.code = code;
    }

}
