package org.entando.kubernetes.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.fabric8.kubernetes.api.model.extensions.IngressStatus;

@JsonSerialize
@JsonDeserialize
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
public class WebServerStatus extends AbstractServerStatus {

    private IngressStatus ingressStatus;

    public WebServerStatus() {
        super();
    }

    public WebServerStatus(String qualifier) {
        super(qualifier);
    }

    public IngressStatus getIngressStatus() {
        return ingressStatus;
    }

    public void setIngressStatus(IngressStatus ingressStatus) {
        this.ingressStatus = ingressStatus;
    }
}